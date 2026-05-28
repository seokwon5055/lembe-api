package com.lembe.ai.client;

public record FalAiRequest(
        String inputImageUrl,  // null = 초기 생성 (입력 사진 없음)
        String prompt
) {}
