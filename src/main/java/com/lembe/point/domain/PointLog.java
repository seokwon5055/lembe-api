package com.lembe.point.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointLog {

    private Long pointLogSeq;
    private Long userSeq;
    private int delta;          // 양수 = 적립, 음수 = 차감
    private int balanceAfter;
    private String reason;      // SIGNUP_BONUS | WEIGHT_LOG | ROULETTE | AI_GEN | PURCHASE | REFUND
    private String refId;       // 연관 ID (외부 결제 ID 등)
    private LocalDateTime createdAt;
}
