package com.lembe.roulette.mapper;

import com.lembe.roulette.domain.RouletteLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RouletteMapper {

    void insert(RouletteLog log);

    RouletteLog findLastFreeByUserSeq(String ucode);

    int countAdBonusTodayByUserSeq(String ucode);
}
