package com.lembe.weight.dto;

import com.lembe.weight.domain.WeightRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WeightHistoryResponse(
        List<WeightPoint> records,
        BigDecimal startWeight,
        BigDecimal currentWeight,
        BigDecimal totalLossKg,
        int periodDays
) {
    public record WeightPoint(
            Long logSeq,
            BigDecimal weightKg,
            BigDecimal bodyFatPct,
            BigDecimal muscleMassKg,
            String source,
            LocalDate loggedAt
    ) {
        public static WeightPoint from(WeightRecord r) {
            return new WeightPoint(r.getLogSeq(), r.getWeightKg(), r.getBodyFatPct(),
                    r.getMuscleMassKg(), r.getSource(), r.getLoggedAt());
        }
    }
}
