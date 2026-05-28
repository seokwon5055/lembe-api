package com.lembe.notification.controller;

import com.lembe.common.dto.ApiResponse;
import com.lembe.notification.dto.NotificationSettingResponse;
import com.lembe.notification.dto.UpdateNotificationSettingRequest;
import com.lembe.notification.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSettingService notificationSettingService;

    @GetMapping("/settings")
    public ApiResponse<NotificationSettingResponse> getSettings(
            @AuthenticationPrincipal Long userSeq) {
        return ApiResponse.ok(notificationSettingService.getSettings(userSeq));
    }

    @PutMapping("/settings")
    public ApiResponse<NotificationSettingResponse> updateSettings(
            @AuthenticationPrincipal Long userSeq,
            @RequestBody UpdateNotificationSettingRequest request) {
        return ApiResponse.ok(notificationSettingService.updateSettings(userSeq, request));
    }
}
