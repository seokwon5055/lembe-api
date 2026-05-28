package com.lembe.ai.client;

import java.math.BigDecimal;

public record FalAiResponse(
        String requestId,
        String imageUrl,
        BigDecimal costUsd
) {}
