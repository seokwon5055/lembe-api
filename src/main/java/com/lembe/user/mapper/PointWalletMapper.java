package com.lembe.user.mapper;

import com.lembe.user.domain.PointWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PointWalletMapper {

    void insert(PointWallet wallet);

    PointWallet findByUserSeq(Long userSeq);

    // 동시성 안전 차감용: SELECT ... FOR UPDATE (트랜잭션 내에서만 사용)
    PointWallet findByUserSeqForUpdate(Long userSeq);

    // 잔액 증감 (delta 양수=적립, 음수=차감). 차감 시 잔액 < 0 방지를 위해 WHERE balance >= ABS(delta) 조건
    int updateBalance(@Param("userSeq") Long userSeq, @Param("delta") int delta);
}
