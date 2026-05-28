package com.lembe.purchase.dto;

import jakarta.validation.constraints.NotBlank;

public record RefundWebhookRequest(
        @NotBlank String platform,       // IOS | ANDROID
        @NotBlank String transactionId,  // 원래 구매의 transaction_id
        String reason                    // 환불 사유 (선택)
) {}
