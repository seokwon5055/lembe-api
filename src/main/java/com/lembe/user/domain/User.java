package com.lembe.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class User {
    private Long userSeq;
    private String email;
    private String password;
    private String nickname;
    private String profileImgUrl;
    private int streakCount;
    private LocalDate lastStreakDt;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
