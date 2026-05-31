package com.lembe.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    private Long profileSeq;
    private String ucode;
    private String gender;          // M | F | N
    private Integer birthYear;
    private BigDecimal heightCm;
    private boolean notificationEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
