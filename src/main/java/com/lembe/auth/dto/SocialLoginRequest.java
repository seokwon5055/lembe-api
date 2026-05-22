package com.lembe.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank String provider,      // KAKAO | APPLE | GOOGLE
        @NotBlank String accessToken
) {}
