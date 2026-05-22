package com.lembe.goal.dto;

import com.lembe.goal.domain.Goal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GoalResponse(
        Long goalSeq,
        String mode,
        String status,
        BigDecimal startWeight,
        BigDecimal targetWeight,
        BigDecimal startBodyFatPct,
        BigDecimal targetBodyFatPct,
        BigDecimal startMuscleMassKg,
        BigDecimal targetMuscleMassKg,
        LocalDateTime createdAt
) {
    public static GoalResponse from(Goal g) {
        return new GoalResponse(
                g.getGoalSeq(),
                g.getMode(),
                g.getStatus(),
                g.getStartWeight(),
                g.getTargetWeight(),
                g.getStartBodyFatPct(),
                g.getTargetBodyFatPct(),
                g.getStartMuscleMassKg(),
                g.getTargetMuscleMassKg(),
                g.getCreatedAt()
        );
    }
}
