package com.lembe.goal.mapper;

import com.lembe.goal.domain.Milestone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MilestoneMapper {

    Milestone findById(Long milestoneSeq);

    List<Milestone> findByGoalSeq(Long goalSeq);

    Milestone findLastUnlockedByGoalSeq(Long goalSeq);

    // LOCKED 마일스톤 중 currentWeight 이하인 것들 (체중 도달 체크)
    List<Milestone> findReachedLocked(@Param("goalSeq") Long goalSeq,
                                      @Param("currentWeight") BigDecimal currentWeight);

    void batchInsert(List<Milestone> milestones);

    void deleteLockedByGoalSeq(Long goalSeq);

    void markReadyToUnlock(Long milestoneSeq);

    void unlock(@Param("milestoneSeq") Long milestoneSeq,
                @Param("photoSeq") Long photoSeq);

    void unlockById(Long milestoneSeq);

    // AI 사진 연동
    Milestone findNextLockedByGoalSeq(@Param("goalSeq") Long goalSeq,
                                      @Param("afterSequence") int afterSequence);

    void updatePhotoSeq(@Param("milestoneSeq") Long milestoneSeq,
                        @Param("photoSeq") Long photoSeq);
}
