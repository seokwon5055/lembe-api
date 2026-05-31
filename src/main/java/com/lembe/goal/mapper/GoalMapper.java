package com.lembe.goal.mapper;

import com.lembe.goal.domain.Goal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoalMapper {

    Goal findActiveByUcode(String ucode);

    Goal findById(Long goalSeq);

    void insert(Goal goal);

    void updateStatus(@Param("goalSeq") Long goalSeq, @Param("status") String status);

    void updateTarget(Goal goal);

    void updateFull(Goal goal);
}
