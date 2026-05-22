package com.lembe.weight.dto;

import com.lembe.weight.domain.WeightRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WeightRecordResponse(
        Long logSeq,
        BigDecimal weightKg,
        BigDecimal bodyFatPct,
        BigDecimal muscleMassKg,
        String source,
        LocalDate loggedAt,
        int streakCount,
        int pointsEarned,
        List<Long> readyToUnlockMilestoneSeqs   // 이번 기록으로 도달한 마일스톤들
) {
    public static WeightRecordResponse of(WeightRecord r, int streakCount,
                                          int pointsEarned, List<Long> milestoneSeqs) {
        return new WeightRecordResponse(
                r.getLogSeq(), r.getWeightKg(), r.getBodyFatPct(),
                r.getMuscleMassKg(), r.getSource(), r.getLoggedAt(),
                streakCount, pointsEarned, milestoneSeqs
        );
    }
}
