package com.lembe.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceToken {
    private Long deviceTokenSeq;
    private String ucode;
    private String fcmToken;
    private String platform;     // IOS | ANDROID
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
