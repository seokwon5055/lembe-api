package com.lembe.purchase.dto;

import jakarta.validation.constraints.NotBlank;

public record PurchaseRequest(
        @NotBlank String platform,       // IOS | ANDROID
        @NotBlank String productId,      // lembe_100p | lembe_600p | ...
        @NotBlank String transactionId,  // 결제 고유 ID (중복 방지)
        @NotBlank String receipt         // 영수증 원본 데이터 / 구글 purchase token
) {}
