package com.lembe.purchase.service;

import com.lembe.purchase.dto.VerificationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 구글 영수증 검증 — 현재 mock
 *
 * TODO: Google Play Developer API 연동 (출시 전)
 *   1. endpoint: GET https://androidpublisher.googleapis.com/androidpublisher/v3/
 *                applications/{packageName}/purchases/products/{productId}/tokens/{purchaseToken}
 *   2. auth: OAuth2 서비스 계정 (service-account.json), scope: androidpublisher
 *   3. response.purchaseState=0: 완료 / purchaseState=2: 대기 / acknowledgementState=1: 승인됨
 *   4. purchaseToken = receipt 필드에 전달
 *   5. 검증 후 acknowledge API 호출 필수 (미승인 시 3일 후 자동 환불)
 *   6. application.yml: google.play.package-name: ${GOOGLE_PACKAGE_NAME}
 */
@Slf4j
@Component
public class GoogleReceiptVerifier implements ReceiptVerifier {

    @Override
    public String platform() {
        return "ANDROID";
    }

    @Override
    public VerificationResult verify(String productId, String transactionId, String receiptData) {
        if (receiptData == null || receiptData.isBlank()) {
            return VerificationResult.fail("구매 토큰이 없습니다.");
        }
        // mock: purchase token 존재하면 검증 성공
        log.info("[Google MOCK] verify: productId={} transactionId={}", productId, transactionId);
        return VerificationResult.ok();
    }
}
