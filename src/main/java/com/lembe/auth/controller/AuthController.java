package com.lembe.auth.controller;

import com.lembe.auth.dto.SocialLoginRequest;
import com.lembe.auth.dto.SocialLoginResponse;
import com.lembe.auth.dto.TokenRefreshRequest;
import com.lembe.auth.service.SocialLoginService;
import com.lembe.common.dto.ApiResponse;
import com.lembe.common.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SocialLoginService socialLoginService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/social-login")
    public ApiResponse<SocialLoginResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        SocialLoginResponse response = socialLoginService.login(request.provider(), request.accessToken());
        return ApiResponse.ok(response);
    }

    @PostMapping("/refresh")
    public ApiResponse<Map<String, String>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        Long userId = jwtTokenProvider.getUserIdFromRefreshToken(request.refreshToken());
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        return ApiResponse.ok(Map.of("accessToken", newAccessToken));
    }
}
