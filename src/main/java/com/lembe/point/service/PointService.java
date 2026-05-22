package com.lembe.point.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.point.domain.PointLog;
import com.lembe.point.mapper.PointLogMapper;
import com.lembe.user.domain.PointWallet;
import com.lembe.user.mapper.PointWalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointWalletMapper pointWalletMapper;
    private final PointLogMapper pointLogMapper;

    /**
     * 포인트 차감 — SELECT FOR UPDATE 로 row lock 후 잔액 검증
     */
    @Transactional
    public int deduct(Long userSeq, int amount, String reason, String refId) {
        PointWallet wallet = pointWalletMapper.findByUserSeqForUpdate(userSeq);
        if (wallet == null || wallet.getBalance() < amount) {
            throw new LembeException(ErrorCode.INSUFFICIENT_POINTS,
                    "포인트가 부족합니다. 필요: " + amount + "P / 보유: "
                    + (wallet != null ? wallet.getBalance() : 0) + "P");
        }

        pointWalletMapper.updateBalance(userSeq, -amount);
        int balanceAfter = wallet.getBalance() - amount;

        pointLogMapper.insert(PointLog.builder()
                .userSeq(userSeq)
                .delta(-amount)
                .balanceAfter(balanceAfter)
                .reason(reason)
                .refId(refId)
                .build());

        return balanceAfter;
    }

    /**
     * 포인트 적립 — 잔액 조회 후 적립 및 로그 기록
     */
    @Transactional
    public int add(Long userSeq, int amount, String reason, String refId) {
        pointWalletMapper.updateBalance(userSeq, amount);
        PointWallet wallet = pointWalletMapper.findByUserSeq(userSeq);
        int balanceAfter = (wallet != null) ? wallet.getBalance() : amount;

        pointLogMapper.insert(PointLog.builder()
                .userSeq(userSeq)
                .delta(amount)
                .balanceAfter(balanceAfter)
                .reason(reason)
                .refId(refId)
                .build());

        return balanceAfter;
    }
}
