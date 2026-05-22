package com.lembe.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointWallet {
    private Long walletSeq;
    private Long userSeq;
    private int balance;
    private LocalDateTime updatedAt;
}
