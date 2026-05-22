package com.lembe.goal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateGoalRequest(
        @NotNull
        @DecimalMin(value = "10.0", message = "목표 체중은 10kg 이상이어야 합니다.")
        BigDecimal targetWeight,

        // 정밀 모드 목표치 (선택)
        BigDecimal targetBodyFatPct,
        BigDecimal targetMuscleMassKg
) {}
