package com.lembe.ai.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * fal.ai mock 구현 — 실제 API 호출 없이 placeholder URL 반환
 *
 * TODO: fal.ai 연동 준비 시
 *   1. build.gradle: okhttp3 또는 RestClient 의존성 추가
 *   2. application.yml: fal.api-key: ${FAL_API_KEY}
 *   3. FalAiClientImpl 작성 후 이 클래스의 @Primary 제거
 */
@Slf4j
@Component
public class FalAiClientMock implements FalAiClient {

    private static final BigDecimal MOCK_COST_USD = new BigDecimal("0.04");
    private static final String PLACEHOLDER_URL =
            "https://placehold.co/512x512/4CAF50/white?text=AI+Preview";

    @Override
    public FalAiResponse generateImage(FalAiRequest request) {
        String requestId = "mock-" + UUID.randomUUID();
        log.info("[FalAi MOCK] generate: requestId={} inputUrl={} prompt={}",
                requestId, request.inputImageUrl(), request.prompt());
        return new FalAiResponse(requestId, PLACEHOLDER_URL, MOCK_COST_USD);
    }
}
