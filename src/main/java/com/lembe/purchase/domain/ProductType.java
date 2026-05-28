package com.lembe.purchase.domain;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {

    LEMBE_100P ("lembe_100p",  "100P 충전",   100,    0,  1200),
    LEMBE_600P ("lembe_600p",  "600P 충전",   600,  100,  5900),
    LEMBE_1500P("lembe_1500p", "1500P 충전", 1500,  500, 12900),
    LEMBE_5000P("lembe_5000p", "5000P 충전", 5000, 2000, 39900);

    private final String productId;
    private final String name;
    private final int points;
    private final int bonusPoints;
    private final int amountKrw;

    public int getTotalPoints() {
        return points + bonusPoints;
    }

    public static ProductType findByProductId(String productId) {
        for (ProductType p : values()) {
            if (p.productId.equals(productId)) return p;
        }
        throw new LembeException(ErrorCode.PRODUCT_NOT_FOUND, "상품 ID: " + productId);
    }
}
