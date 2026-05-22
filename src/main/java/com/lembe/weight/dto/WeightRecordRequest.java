package com.lembe.weight.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WeightRecordRequest(
        @NotNull
        @DecimalMin(value = "10.0", message = "체중은 10kg 이상이어야 합니다.")
        BigDecimal weightKg,

        BigDecimal bodyFatPct,
        BigDecimal muscleMassKg,

        @NotNull
        @Pattern(regexp = "MANUAL|HEALTHKIT|SAMSUNG_HEALTH",
                 message = "source는 MANUAL, HEALTHKIT, SAMSUNG_HEALTH 중 하나여야 합니다.")
        String source,

        @NotNull
        LocalDate loggedAt
) {}
