package com.lembe.purchase.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Purchase {

    private Long purchaseSeq;
    private Long userSeq;
    private String platform;         // IOS | ANDROID
    private String productId;
    private String transactionId;
    private String receipt;
    private int pointsGranted;       // 기본 포인트
    private int bonusPoints;         // 보너스 포인트
    private int amountKrw;
    private String status;           // PENDING | VERIFIED | FAILED | REFUNDED
    private LocalDateTime verifiedAt;
    private LocalDateTime refundedAt;
    private String refundReason;
    private LocalDateTime createdAt;
}
