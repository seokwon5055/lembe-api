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
public class PointWallet {
    private Long walletSeq;
    private String ucode;
    private int balance;
    private LocalDateTime updatedAt;
}
