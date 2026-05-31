package com.lembe.weight.mapper;

import com.lembe.weight.domain.WeightRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WeightMapper {

    void insert(WeightRecord record);

    WeightRecord findByUcodeAndDate(@Param("ucode") String ucode,
                                   @Param("loggedAt") LocalDate loggedAt);

    WeightRecord findLatestByUserSeq(String ucode);

    List<WeightRecord> findHistory(@Param("ucode") String ucode,
                                   @Param("fromDate") LocalDate fromDate);
}
