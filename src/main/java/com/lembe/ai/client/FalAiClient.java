package com.lembe.ai.client;

public interface FalAiClient {

    /**
     * 이미지 생성 요청.
     * TODO: fal.ai 연동 시 FalAiClientImpl 로 교체
     *   - endpoint: POST https://queue.fal.run/fal-ai/flux/dev
     *   - auth: Authorization: Key ${FAL_API_KEY}
     *   - 결과 폴링: GET https://queue.fal.run/fal-ai/flux/dev/requests/{requestId}
     */
    FalAiResponse generateImage(FalAiRequest request);
}
