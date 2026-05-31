package com.lembe.point.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointLog {

    private Long pointLogSeq;
    private String ucode;
    private int delta;          // 양수 = 적립, 음수 = 차감
    private int balanceAfter;
    private String reason;      // SIGNUP_BONUS | WEIGHT_LOG | ROULETTE | AI_GEN | PURCHASE | REFUND
    private String refId;       // 연관 ID (외부 결제 ID 등)
    private LocalDateTime createdAt;
}
