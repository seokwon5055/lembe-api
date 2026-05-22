package com.lembe.weight.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class WeightRecord {

    private Long logSeq;
    private Long userSeq;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPct;
    private BigDecimal muscleMassKg;
    private String source;          // MANUAL | HEALTHKIT | SAMSUNG_HEALTH
    private LocalDate loggedAt;
    private LocalDateTime createdAt;
}
