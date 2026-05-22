package com.lembe.goal.dto;

import com.lembe.goal.domain.Milestone;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MilestoneResponse(
        Long milestoneSeq,
        int sequence,
        String label,
        BigDecimal targetWeight,
        BigDecimal lossKg,
        BigDecimal targetBodyFatPct,
        BigDecimal targetMuscleMassKg,
        boolean isFinal,
        String status,
        Long photoSeq,
        LocalDateTime unlockedAt
) {
    public static MilestoneResponse from(Milestone m) {
        return new MilestoneResponse(
                m.getMilestoneSeq(),
                m.getSequence(),
                m.getLabel(),
                m.getTargetWeight(),
                m.getLossKg(),
                m.getTargetBodyFatPct(),
                m.getTargetMuscleMassKg(),
                m.isFinal(),
                m.getStatus(),
                m.getPhotoSeq(),
                m.getUnlockedAt()
        );
    }
}
