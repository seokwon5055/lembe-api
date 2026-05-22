package com.lembe.user.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.user.dto.UserMeResponse;
import com.lembe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal Long userSeq) {
        return ApiResponse.ok(userService.getMe(userSeq));
    }
}
