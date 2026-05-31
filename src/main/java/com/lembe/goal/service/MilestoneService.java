package com.lembe.goal.service;

import com.lembe.ai.event.MilestoneUnlockedEvent;
import com.lembe.ai.event.ProgressPhotoUploadedEvent;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.goal.domain.Milestone;
import com.lembe.goal.dto.MilestoneResponse;
import com.lembe.goal.mapper.MilestoneMapper;
import com.lembe.notification.domain.NotificationType;
import com.lembe.notification.service.NotificationService;
import com.lembe.photo.dto.PhotoResponse;
import com.lembe.photo.service.PhotoService;
import com.lembe.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private static final int UNLOCK_COST = 15;

    private final MilestoneMapper milestoneMapper;
    private final PointService pointService;
    private final NotificationService notificationService;
    private final PhotoService photoService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MilestoneResponse unlock(String ucode, Long milestoneSeq) {
        Milestone milestone = milestoneMapper.findById(milestoneSeq);
        if (milestone == null || !milestone.getUcode().equals(ucode)) {
            throw new LembeException(ErrorCode.MILESTONE_NOT_FOUND);
        }

        String status = milestone.getStatus();
        if ("UNLOCKED".equals(status)) {
            throw new LembeException(ErrorCode.MILESTONE_ALREADY_UNLOCKED);
        }
        if (!"READY_TO_UNLOCK".equals(status)) {
            throw new LembeException(ErrorCode.MILESTONE_NOT_READY);
        }

        pointService.deduct(ucode, UNLOCK_COST, "MILESTONE_UNLOCK", String.valueOf(milestoneSeq));
        milestoneMapper.unlockById(milestoneSeq);

        notificationService.sendToUser(ucode, NotificationType.MILESTONE_UNLOCKED);

        // 다음 마일스톤 AI 사진 생성 트리거 (커밋 후 비동기 실행)
        eventPublisher.publishEvent(new MilestoneUnlockedEvent(ucode, milestoneSeq));

        return MilestoneResponse.from(milestoneMapper.findById(milestoneSeq));
    }

    @Transactional
    public PhotoResponse uploadProgressPhoto(String ucode, Long milestoneSeq, MultipartFile file) {
        Milestone milestone = milestoneMapper.findById(milestoneSeq);
        if (milestone == null || !milestone.getUcode().equals(ucode)) {
            throw new LembeException(ErrorCode.MILESTONE_NOT_FOUND);
        }

        PhotoResponse photo = photoService.upload(ucode, file, "PROGRESS", milestoneSeq);

        // 적응 학습 트리거 (커밋 후 비동기 실행)
        eventPublisher.publishEvent(
                new ProgressPhotoUploadedEvent(ucode, milestoneSeq, photo.photoSeq()));

        return photo;
    }
}
