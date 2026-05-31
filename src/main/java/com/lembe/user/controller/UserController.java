package com.lembe.user.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.user.dto.UpdateProfileRequest;
import com.lembe.user.dto.UserMeResponse;
import com.lembe.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal String ucode) {
        return ApiResponse.ok(userService.getMe(ucode));
    }

    @GetMapping("/check-nickname")
    public ApiResponse<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean available = userService.checkNickname(nickname);
        return ApiResponse.ok(Map.of("available", available));
    }

    @PutMapping("/me/profile")
    public ApiResponse<Void> updateProfile(
            @AuthenticationPrincipal String ucode,
            @Valid @RequestBody UpdateProfileRequest req) {
        userService.updateProfile(ucode, req);
        return ApiResponse.ok(null);
    }
}
