package com.lembe.goal.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.goal.domain.Milestone;
import com.lembe.goal.dto.MilestoneResponse;
import com.lembe.goal.mapper.MilestoneMapper;
import com.lembe.notification.domain.NotificationType;
import com.lembe.notification.service.NotificationService;
import com.lembe.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private static final int UNLOCK_COST = 15;

    private final MilestoneMapper milestoneMapper;
    private final PointService pointService;
    private final NotificationService notificationService;

    @Transactional
    public MilestoneResponse unlock(Long userSeq, Long milestoneSeq) {
        Milestone milestone = milestoneMapper.findById(milestoneSeq);
        if (milestone == null || !milestone.getUserSeq().equals(userSeq)) {
            throw new LembeException(ErrorCode.MILESTONE_NOT_FOUND);
        }

        String status = milestone.getStatus();
        if ("UNLOCKED".equals(status)) {
            throw new LembeException(ErrorCode.MILESTONE_ALREADY_UNLOCKED);
        }
        if (!"READY_TO_UNLOCK".equals(status)) {
            throw new LembeException(ErrorCode.MILESTONE_NOT_READY);
        }

        pointService.deduct(userSeq, UNLOCK_COST, "MILESTONE_UNLOCK", String.valueOf(milestoneSeq));
        milestoneMapper.unlockById(milestoneSeq);

        notificationService.sendToUser(userSeq, NotificationType.MILESTONE_UNLOCKED);

        // TODO: fal.ai 연동 시 다음 마일스톤 AI 사진 생성 트리거

        return MilestoneResponse.from(milestoneMapper.findById(milestoneSeq));
    }
}
