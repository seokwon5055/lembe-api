package com.lembe.point.mapper;

import com.lembe.point.domain.PointLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointLogMapper {

    void insert(PointLog pointLog);

    List<PointLog> findHistory(@Param("ucode") String ucode,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    int countByUserSeq(String ucode);
}
