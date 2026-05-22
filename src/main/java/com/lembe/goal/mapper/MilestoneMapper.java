package com.lembe.goal.mapper;

import com.lembe.goal.domain.Milestone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MilestoneMapper {

    List<Milestone> findByGoalSeq(Long goalSeq);

    Milestone findLastUnlockedByGoalSeq(Long goalSeq);

    void batchInsert(List<Milestone> milestones);

    void deleteLockedByGoalSeq(Long goalSeq);

    void unlock(@Param("milestoneSeq") Long milestoneSeq,
                @Param("photoSeq") Long photoSeq);
}
