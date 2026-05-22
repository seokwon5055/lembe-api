package com.lembe.goal.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Milestone {

    private Long milestoneSeq;
    private Long goalSeq;
    private Long userSeq;
    private int sequence;           // 순서 (sort_order 컬럼)
    private String label;           // e.g. "-3kg"
    private BigDecimal targetWeight;
    private BigDecimal lossKg;
    private BigDecimal targetBodyFatPct;
    private BigDecimal targetMuscleMassKg;
    private boolean isFinal;
    private String status;          // LOCKED | UNLOCKED
    private Long photoSeq;
    private LocalDateTime unlockedAt;
    private LocalDateTime createdAt;
}
