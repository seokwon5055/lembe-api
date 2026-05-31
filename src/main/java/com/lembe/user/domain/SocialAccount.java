package com.lembe.user.domain;

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
public class SocialAccount {
    private Long socialAccountSeq;
    private String ucode;
    private String provider;    // KAKAO | APPLE | GOOGLE
    private String providerId;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
