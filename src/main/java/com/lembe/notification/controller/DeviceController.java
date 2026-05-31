package com.lembe.notification.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.notification.dto.DeviceTokenRequest;
import com.lembe.notification.service.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping("/token")
    public ApiResponse<Void> registerToken(
            @AuthenticationPrincipal String ucode,
            @Valid @RequestBody DeviceTokenRequest request) {
        deviceTokenService.registerToken(ucode, request);
        return ApiResponse.ok();
    }
}
