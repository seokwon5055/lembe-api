package com.lembe.purchase.service;

import com.lembe.purchase.dto.VerificationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 애플 영수증 검증 — 현재 mock
 *
 * TODO: App Store Server API 연동 (출시 전)
 *   1. endpoint: POST https://buy.itunes.apple.com/verifyReceipt (sandbox: https://sandbox.itunes.apple.com/verifyReceipt)
 *   2. body: {"receipt-data": "<base64>", "password": "${APPLE_SHARED_SECRET}", "exclude-old-transactions": true}
 *   3. response status=0: 성공 / status=21007: sandbox 영수증 (prod 환경에서 sandbox로 재시도)
 *   4. in_app[].transaction_id, in_app[].product_id 교차 검증 필수
 *   5. application.yml: apple.shared-secret: ${APPLE_SHARED_SECRET}
 */
@Slf4j
@Component
public class AppleReceiptVerifier implements ReceiptVerifier {

    @Override
    public String platform() {
        return "IOS";
    }

    @Override
    public VerificationResult verify(String productId, String transactionId, String receiptData) {
        if (receiptData == null || receiptData.isBlank()) {
            return VerificationResult.fail("영수증 데이터가 없습니다.");
        }
        // mock: receipt_data 존재하면 검증 성공
        log.info("[Apple MOCK] verify: productId={} transactionId={}", productId, transactionId);
        return VerificationResult.ok();
    }
}
