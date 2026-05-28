package com.lembe.notification.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DeviceToken {
    private Long deviceTokenSeq;
    private Long userSeq;
    private String fcmToken;
    private String platform;     // IOS | ANDROID
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
