package com.lembe.purchase.service;

import com.lembe.purchase.dto.VerificationResult;

public interface ReceiptVerifier {

    /** 이 verifier가 처리하는 플랫폼 (IOS | ANDROID) */
    String platform();

    /**
     * 영수증 검증
     *
     * @param productId     구매한 상품 ID
     * @param transactionId 결제 고유 ID
     * @param receiptData   영수증 원본 (애플: base64 / 구글: purchase token)
     */
    VerificationResult verify(String productId, String transactionId, String receiptData);
}
