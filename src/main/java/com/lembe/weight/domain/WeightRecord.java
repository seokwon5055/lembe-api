package com.lembe.weight.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightRecord {

    private Long logSeq;
    private String ucode;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPct;
    private BigDecimal muscleMassKg;
    private String source;          // MANUAL | HEALTHKIT | SAMSUNG_HEALTH
    private LocalDate loggedAt;
    private LocalDateTime createdAt;
}
