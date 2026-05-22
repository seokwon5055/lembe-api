package com.lembe.weight.mapper;

import com.lembe.weight.domain.WeightRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WeightMapper {

    void insert(WeightRecord record);

    WeightRecord findByUserAndDate(@Param("userSeq") Long userSeq,
                                   @Param("loggedAt") LocalDate loggedAt);

    WeightRecord findLatestByUserSeq(Long userSeq);

    List<WeightRecord> findHistory(@Param("userSeq") Long userSeq,
                                   @Param("fromDate") LocalDate fromDate);
}
