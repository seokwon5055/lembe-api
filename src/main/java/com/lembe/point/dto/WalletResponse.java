package com.lembe.point.dto;

import com.lembe.user.domain.PointWallet;

import java.time.LocalDateTime;

public record WalletResponse(
        Long walletSeq,
        int balance,
        LocalDateTime updatedAt
) {
    public static WalletResponse from(PointWallet w) {
        return new WalletResponse(w.getWalletSeq(), w.getBalance(), w.getUpdatedAt());
    }
}
