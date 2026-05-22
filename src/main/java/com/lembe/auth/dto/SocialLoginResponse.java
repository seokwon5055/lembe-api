package com.lembe.auth.dto;

public record SocialLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewUser
) {}
