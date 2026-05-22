package com.lembe.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserProfile {
    private Long profileSeq;
    private Long userSeq;
    private String gender;          // M | F | N
    private Integer birthYear;
    private BigDecimal heightCm;
    private boolean notificationEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
