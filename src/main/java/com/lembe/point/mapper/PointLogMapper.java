package com.lembe.point.mapper;

import com.lembe.point.domain.PointLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointLogMapper {

    void insert(PointLog pointLog);
}
