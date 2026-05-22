package com.lembe.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SocialAccount {
    private Long socialAccountSeq;
    private Long userSeq;
    private String provider;    // KAKAO | APPLE | GOOGLE
    private String providerId;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
