package com.lembe.purchase.mapper;

import com.lembe.purchase.domain.Purchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    void insert(Purchase purchase);

    Purchase findById(Long purchaseSeq);

    Purchase findByPlatformAndTransactionId(@Param("platform") String platform,
                                            @Param("transactionId") String transactionId);

    boolean existsByPlatformAndTransactionId(@Param("platform") String platform,
                                             @Param("transactionId") String transactionId);

    List<Purchase> findByUcode(@Param("ucode") String ucode,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    int countByUserSeq(String ucode);

    void updateVerified(Long purchaseSeq);

    void updateFailed(Long purchaseSeq);

    void updateRefunded(@Param("purchaseSeq") Long purchaseSeq,
                        @Param("refundReason") String refundReason);
}
