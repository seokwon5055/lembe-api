package com.lembe.goal.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.goal.domain.Goal;
import com.lembe.goal.domain.Milestone;
import com.lembe.goal.dto.CreateGoalRequest;
import com.lembe.goal.dto.GoalResponse;
import com.lembe.goal.dto.GoalWithMilestonesResponse;
import com.lembe.goal.dto.MilestoneResponse;
import com.lembe.goal.dto.UpdateGoalRequest;
import com.lembe.ai.event.GoalCreatedEvent;
import com.lembe.goal.mapper.GoalMapper;
import com.lembe.goal.mapper.MilestoneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalMapper goalMapper;
    private final MilestoneMapper milestoneMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final MilestoneCalculator calculator = new MilestoneCalculator();

    @Transactional
    public GoalWithMilestonesResponse createGoal(Long userSeq, CreateGoalRequest req) {
        if (req.targetWeight().compareTo(req.startWeight()) >= 0) {
            throw new LembeException(ErrorCode.INVALID_INPUT, "목표 체중은 시작 체중보다 낮아야 합니다.");
        }

        // 기존 활성 목표 포기 처리
        Goal existing = goalMapper.findActiveByUserSeq(userSeq);
        if (existing != null) {
            goalMapper.updateStatus(existing.getGoalSeq(), "ABANDONED");
        }

        // 목표 생성
        Goal goal = Goal.builder()
                .userSeq(userSeq)
                .mode(req.mode())
                .startWeight(req.startWeight())
                .targetWeight(req.targetWeight())
                .startBodyFatPct(req.startBodyFatPct())
                .targetBodyFatPct(req.targetBodyFatPct())
                .startMuscleMassKg(req.startMuscleMassKg())
                .targetMuscleMassKg(req.targetMuscleMassKg())
                .build();
        goalMapper.insert(goal);

        // 마일스톤 자동 생성
        List<Milestone> milestones = buildMilestones(goal, req.startWeight(), req.targetWeight(), 1);
        if (!milestones.isEmpty()) {
            milestoneMapper.batchInsert(milestones);
        }

        // AI 사진 생성 트리거 (커밋 후 비동기 실행)
        eventPublisher.publishEvent(new GoalCreatedEvent(userSeq, goal.getGoalSeq()));

        return toResponse(goal, milestones);
    }

    @Transactional(readOnly = true)
    public GoalWithMilestonesResponse getCurrentGoal(Long userSeq) {
        Goal goal = requireActiveGoal(userSeq);
        List<Milestone> milestones = milestoneMapper.findByGoalSeq(goal.getGoalSeq());
        return toResponse(goal, milestones);
    }

    @Transactional
    public GoalWithMilestonesResponse updateGoal(Long userSeq, Long goalSeq, UpdateGoalRequest req) {
        Goal goal = goalMapper.findById(goalSeq);
        if (goal == null || !goal.getUserSeq().equals(userSeq)) {
            throw new LembeException(ErrorCode.GOAL_NOT_FOUND);
        }
        if (!"ACTIVE".equals(goal.getStatus())) {
            throw new LembeException(ErrorCode.INVALID_INPUT, "활성 목표만 변경할 수 있습니다.");
        }
        if (req.targetWeight().compareTo(goal.getStartWeight()) >= 0) {
            throw new LembeException(ErrorCode.INVALID_INPUT, "목표 체중은 시작 체중보다 낮아야 합니다.");
        }

        // 목표 수치 업데이트
        Goal updated = Goal.builder()
                .goalSeq(goalSeq)
                .targetWeight(req.targetWeight())
                .targetBodyFatPct(req.targetBodyFatPct())
                .targetMuscleMassKg(req.targetMuscleMassKg())
                .build();
        goalMapper.updateTarget(updated);

        // LOCKED 마일스톤 삭제 후 재계산
        milestoneMapper.deleteLockedByGoalSeq(goalSeq);

        // 마지막 달성 기준점 결정
        Milestone lastUnlocked = milestoneMapper.findLastUnlockedByGoalSeq(goalSeq);
        BigDecimal baseWeight = (lastUnlocked != null) ? lastUnlocked.getTargetWeight() : goal.getStartWeight();
        int nextSeq = (lastUnlocked != null) ? lastUnlocked.getSequence() + 1 : 1;

        if (req.targetWeight().compareTo(baseWeight) >= 0) {
            // 이미 목표 초과 달성 상태
            return getCurrentGoal(userSeq);
        }

        List<Milestone> newMilestones = buildMilestones(
                Goal.builder()
                        .goalSeq(goalSeq)
                        .userSeq(userSeq)
                        .startWeight(goal.getStartWeight())
                        .targetWeight(req.targetWeight())
                        .startBodyFatPct(goal.getStartBodyFatPct())
                        .targetBodyFatPct(req.targetBodyFatPct())
                        .startMuscleMassKg(goal.getStartMuscleMassKg())
                        .targetMuscleMassKg(req.targetMuscleMassKg())
                        .build(),
                baseWeight,
                req.targetWeight(),
                nextSeq
        );

        if (!newMilestones.isEmpty()) {
            milestoneMapper.batchInsert(newMilestones);
        }

        return getCurrentGoal(userSeq);
    }

    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestones(Long userSeq) {
        Goal goal = requireActiveGoal(userSeq);
        return milestoneMapper.findByGoalSeq(goal.getGoalSeq())
                .stream().map(MilestoneResponse::from).toList();
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private Goal requireActiveGoal(Long userSeq) {
        Goal goal = goalMapper.findActiveByUserSeq(userSeq);
        if (goal == null) {
            throw new LembeException(ErrorCode.GOAL_NOT_FOUND, "활성 목표가 없습니다.");
        }
        return goal;
    }

    private List<Milestone> buildMilestones(Goal goal, BigDecimal fromWeight,
                                             BigDecimal toWeight, int startSeq) {
        List<MilestoneCalculator.MilestoneCalcResult> results = calculator.calculate(
                fromWeight, toWeight, startSeq,
                goal.getStartBodyFatPct(), goal.getTargetBodyFatPct(),
                goal.getStartMuscleMassKg(), goal.getTargetMuscleMassKg()
        );

        return results.stream().map(r -> Milestone.builder()
                .goalSeq(goal.getGoalSeq())
                .userSeq(goal.getUserSeq())
                .sequence(r.sequence())
                .label(r.label())
                .targetWeight(r.targetWeight())
                .lossKg(r.lossKg())
                .targetBodyFatPct(r.targetBodyFatPct())
                .targetMuscleMassKg(r.targetMuscleMassKg())
                .isFinal(r.isFinal())
                .status("LOCKED")
                .build()
        ).toList();
    }

    private GoalWithMilestonesResponse toResponse(Goal goal, List<Milestone> milestones) {
        return new GoalWithMilestonesResponse(
                GoalResponse.from(goal),
                milestones.stream().map(MilestoneResponse::from).toList()
        );
    }
}
