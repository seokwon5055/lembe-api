package com.lembe.point.dto;

import com.lembe.point.domain.PointLog;

import java.time.LocalDateTime;

public record PointTransactionResponse(
        Long pointLogSeq,
        int delta,
        int balanceAfter,
        String reason,
        String refId,
        LocalDateTime createdAt
) {
    public static PointTransactionResponse from(PointLog log) {
        return new PointTransactionResponse(
                log.getPointLogSeq(), log.getDelta(), log.getBalanceAfter(),
                log.getReason(), log.getRefId(), log.getCreatedAt()
        );
    }
}
