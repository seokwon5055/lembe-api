package com.lembe.auth.service;

public record SocialUserInfo(
        String providerId,
        String email,
        String nickname,
        String profileImageUrl
) {}
