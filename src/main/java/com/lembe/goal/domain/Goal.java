package com.lembe.goal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    private Long goalSeq;
    private String ucode;
    private String mode;        // SIMPLE | PRECISE
    private String status;      // ACTIVE | COMPLETED | ABANDONED
    private BigDecimal startWeight;
    private BigDecimal targetWeight;
    private BigDecimal startBodyFatPct;
    private BigDecimal targetBodyFatPct;
    private BigDecimal startMuscleMassKg;
    private BigDecimal targetMuscleMassKg;
    private int aiRegenCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
