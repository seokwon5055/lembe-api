package com.lembe.purchase.dto;

import com.lembe.purchase.domain.ProductType;
import com.lembe.purchase.domain.Purchase;

import java.time.LocalDateTime;

public record PurchaseResponse(
        Long purchaseSeq,
        String platform,
        String productId,
        String productName,
        int pointsGranted,
        int bonusPoints,
        int totalPoints,
        int amountKrw,
        String status,
        int walletBalance,
        LocalDateTime createdAt
) {
    public static PurchaseResponse from(Purchase purchase, ProductType product, int walletBalance) {
        return new PurchaseResponse(
                purchase.getPurchaseSeq(),
                purchase.getPlatform(),
                purchase.getProductId(),
                product.getName(),
                purchase.getPointsGranted(),
                purchase.getBonusPoints(),
                purchase.getPointsGranted() + purchase.getBonusPoints(),
                purchase.getAmountKrw(),
                purchase.getStatus(),
                walletBalance,
                purchase.getCreatedAt()
        );
    }
}
