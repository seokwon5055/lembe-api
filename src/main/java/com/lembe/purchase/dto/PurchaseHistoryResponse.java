package com.lembe.purchase.dto;

import com.lembe.purchase.domain.Purchase;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseHistoryResponse(
        List<Item> items,
        int total,
        int page,
        int size
) {
    public record Item(
            Long purchaseSeq,
            String platform,
            String productId,
            int pointsGranted,
            int bonusPoints,
            int amountKrw,
            String status,
            LocalDateTime createdAt,
            LocalDateTime refundedAt
    ) {
        public static Item from(Purchase p) {
            return new Item(
                    p.getPurchaseSeq(),
                    p.getPlatform(),
                    p.getProductId(),
                    p.getPointsGranted(),
                    p.getBonusPoints(),
                    p.getAmountKrw(),
                    p.getStatus(),
                    p.getCreatedAt(),
                    p.getRefundedAt()
            );
        }
    }
}
