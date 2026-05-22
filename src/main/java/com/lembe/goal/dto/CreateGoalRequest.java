package com.lembe.goal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateGoalRequest(
        @NotBlank
        @Pattern(regexp = "SIMPLE|PRECISE", message = "mode는 SIMPLE 또는 PRECISE 이어야 합니다.")
        String mode,

        @NotNull
        @DecimalMin(value = "10.0", message = "시작 체중은 10kg 이상이어야 합니다.")
        BigDecimal startWeight,

        @NotNull
        @DecimalMin(value = "10.0", message = "목표 체중은 10kg 이상이어야 합니다.")
        BigDecimal targetWeight,

        // 정밀 모드 선택 입력
        BigDecimal startBodyFatPct,
        BigDecimal targetBodyFatPct,
        BigDecimal startMuscleMassKg,
        BigDecimal targetMuscleMassKg
) {}
