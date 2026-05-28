package com.lembe.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DeviceTokenRequest(
        @NotBlank String fcmToken,
        @NotBlank @Pattern(regexp = "IOS|ANDROID") String platform
) {}
