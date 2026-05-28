package com.lembe.ai.service;

import com.lembe.ai.client.FalAiClient;
import com.lembe.ai.client.FalAiRequest;
import com.lembe.ai.client.FalAiResponse;
import com.lembe.ai.domain.AiGenerationJob;
import com.lembe.ai.dto.AiJobResponse;
import com.lembe.ai.mapper.AiGenerationMapper;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.goal.domain.Milestone;
import com.lembe.goal.mapper.MilestoneMapper;
import com.lembe.photo.domain.Photo;
import com.lembe.photo.mapper.PhotoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGenerationService {

    private final AiGenerationMapper aiGenerationMapper;
    private final MilestoneMapper milestoneMapper;
    private final PhotoMapper photoMapper;
    private final FalAiClient falAiClient;
    private final AiCostGuardService costGuard;

    // ── 이벤트 핸들러에서 호출 ──────────────────────────────────────────────────

    /**
     * 목표 생성 시: 첫 마일스톤 + 최종 마일스톤 AI 사진 생성 (2장)
     */
    @Transactional
    public void generateInitial(Long userSeq, Long goalSeq) {
        costGuard.checkGlobalLimit();

        List<Milestone> milestones = milestoneMapper.findByGoalSeq(goalSeq);
        if (milestones.isEmpty()) return;

        Milestone first = milestones.get(0);
        Milestone finalMs = milestones.stream()
                .filter(Milestone::isFinal)
                .findFirst()
                .orElse(milestones.get(milestones.size() - 1));

        processJob(userSeq, first, "INITIAL", null);

        if (!first.getMilestoneSeq().equals(finalMs.getMilestoneSeq())) {
            processJob(userSeq, finalMs, "INITIAL", null);
        }
    }

    /**
     * 마일스톤 unlock 후: 다음 LOCKED 마일스톤 AI 사진 생성
     */
    @Transactional
    public void generateNext(Long unlockedMilestoneSeq) {
        Milestone current = milestoneMapper.findById(unlockedMilestoneSeq);
        if (current == null) return;

        Milestone next = milestoneMapper.findNextLockedByGoalSeq(
                current.getGoalSeq(), current.getSequence());
        if (next == null) return;

        costGuard.checkGlobalLimit();
        processJob(current.getUserSeq(), next, "MILESTONE", null);
    }

    /**
     * 실제 경과 사진 업로드 시: 다음 + 최종 마일스톤 AI 사진 갱신 (적응 학습)
     */
    @Transactional
    public void adaptiveUpdate(Long userSeq, Long milestoneSeq, Long realPhotoSeq) {
        Milestone milestone = milestoneMapper.findById(milestoneSeq);
        if (milestone == null) return;

        // TODO: fal.ai Vision 연동 시 실제 체형 분석 후 프롬프트 보정
        log.info("[AI Adaptive] vision mock: milestoneSeq={} realPhoto={}", milestoneSeq, realPhotoSeq);

        costGuard.checkGlobalLimit();

        Photo realPhoto = photoMapper.findById(realPhotoSeq);
        String inputUrl = (realPhoto != null) ? realPhoto.getCdnUrl() : null;

        // 다음 마일스톤 갱신
        Milestone next = milestoneMapper.findNextLockedByGoalSeq(
                milestone.getGoalSeq(), milestone.getSequence());
        if (next != null) {
            processJob(userSeq, next, "ADAPTIVE", inputUrl);
        }

        // 최종 마일스톤 갱신
        List<Milestone> all = milestoneMapper.findByGoalSeq(milestone.getGoalSeq());
        all.stream()
                .filter(Milestone::isFinal)
                .filter(m -> "LOCKED".equals(m.getStatus()))
                .findFirst()
                .ifPresent(finalMs -> processJob(userSeq, finalMs, "FINAL_UPDATE", inputUrl));
    }

    // ── API 호출 ───────────────────────────────────────────────────────────────

    /**
     * 마일스톤 AI 사진 재생성
     */
    @Transactional
    public AiJobResponse regenerate(Long userSeq, Long milestoneSeq, boolean isFreeSameImage) {
        Milestone milestone = milestoneMapper.findById(milestoneSeq);
        if (milestone == null || !milestone.getUserSeq().equals(userSeq)) {
            throw new LembeException(ErrorCode.MILESTONE_NOT_FOUND);
        }

        if (isFreeSameImage && aiGenerationMapper.existsFreeRetry(userSeq, milestoneSeq)) {
            throw new LembeException(ErrorCode.AI_FREE_RETRY_EXHAUSTED);
        }

        costGuard.checkUserLimit(userSeq);
        costGuard.checkGlobalLimit();

        AiGenerationJob job = buildJob(userSeq, milestone, "MILESTONE", isFreeSameImage, null);
        aiGenerationMapper.insert(job);

        try {
            FalAiResponse aiResult = callFalAi(null, buildPrompt(milestone));
            Long resultPhotoSeq = saveAiPhoto(userSeq, milestone, aiResult.imageUrl());

            aiGenerationMapper.updateCompleted(
                    job.getGenSeq(), resultPhotoSeq, aiResult.requestId(), aiResult.costUsd());
            milestoneMapper.updatePhotoSeq(milestoneSeq, resultPhotoSeq);

            return AiJobResponse.from(aiGenerationMapper.findById(job.getGenSeq()),
                    aiResult.imageUrl());
        } catch (Exception e) {
            aiGenerationMapper.updateFailed(job.getGenSeq(), e.getMessage());
            throw new LembeException(ErrorCode.AI_GENERATION_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public AiJobResponse getJob(Long genSeq) {
        AiGenerationJob job = aiGenerationMapper.findById(genSeq);
        if (job == null) throw new LembeException(ErrorCode.AI_JOB_NOT_FOUND);

        String resultUrl = null;
        if (job.getResultPhotoSeq() != null) {
            Photo photo = photoMapper.findById(job.getResultPhotoSeq());
            if (photo != null) resultUrl = photo.getCdnUrl();
        }
        return AiJobResponse.from(job, resultUrl);
    }

    // ── internal helpers ───────────────────────────────────────────────────────

    private void processJob(Long userSeq, Milestone milestone, String jobType, String inputUrl) {
        AiGenerationJob job = buildJob(userSeq, milestone, jobType, false, null);
        aiGenerationMapper.insert(job);

        try {
            FalAiResponse aiResult = callFalAi(inputUrl, buildPrompt(milestone));
            Long resultPhotoSeq = saveAiPhoto(userSeq, milestone, aiResult.imageUrl());

            aiGenerationMapper.updateCompleted(
                    job.getGenSeq(), resultPhotoSeq, aiResult.requestId(), aiResult.costUsd());
            milestoneMapper.updatePhotoSeq(milestone.getMilestoneSeq(), resultPhotoSeq);

            log.info("[AI] job completed: genSeq={} milestone={} type={} photo={}",
                    job.getGenSeq(), milestone.getMilestoneSeq(), jobType, resultPhotoSeq);
        } catch (Exception e) {
            aiGenerationMapper.updateFailed(job.getGenSeq(), e.getMessage());
            log.error("[AI] job failed: genSeq={} milestone={} type={}",
                    job.getGenSeq(), milestone.getMilestoneSeq(), jobType, e);
        }
    }

    private FalAiResponse callFalAi(String inputUrl, String prompt) {
        // TODO: fal.ai 연동 시 실제 사진 URL 전달 (getCurrentPhotoUrl(userSeq))
        return falAiClient.generateImage(new FalAiRequest(inputUrl, prompt));
    }

    private Long saveAiPhoto(Long userSeq, Milestone milestone, String imageUrl) {
        // 기존 사진이 있으면 version+1 + parent 참조
        int version = 1;
        Long parentPhotoSeq = null;
        if (milestone.getPhotoSeq() != null) {
            Photo old = photoMapper.findById(milestone.getPhotoSeq());
            if (old != null) {
                version = old.getVersion() + 1;
                parentPhotoSeq = old.getPhotoSeq();
            }
        }

        Photo photo = Photo.builder()
                .userSeq(userSeq)
                .milestoneSeq(milestone.getMilestoneSeq())
                .photoType("AI_GENERATED")
                .storageKey("mock/ai/" + UUID.randomUUID())
                .cdnUrl(imageUrl)
                .isMain(false)
                .version(version)
                .parentPhotoSeq(parentPhotoSeq)
                .takenAt(LocalDateTime.now())
                .build();

        photoMapper.insert(photo);
        return photo.getPhotoSeq();
    }

    private AiGenerationJob buildJob(Long userSeq, Milestone milestone,
                                      String jobType, boolean isFreeRetry,
                                      Long inputPhotoSeq) {
        return AiGenerationJob.builder()
                .userSeq(userSeq)
                .jobType(jobType)
                .milestoneSeq(milestone.getMilestoneSeq())
                .inputPhotoSeq(inputPhotoSeq)
                .status("PENDING")
                .prompt(buildPrompt(milestone))
                .isFreeRetry(isFreeRetry)
                .costUsd(BigDecimal.ZERO)
                .retryCount(0)
                .build();
    }

    private String buildPrompt(Milestone milestone) {
        return String.format(
                "Fitness transformation to milestone %s: target %.1fkg",
                milestone.getLabel(), milestone.getTargetWeight());
    }
}
