package com.lembe.notification.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SchedulerQueryMapper {

    /** 오늘 체중 기록이 없는 활성 사용자 */
    List<Long> findUserSeqsWithNoWeightLogToday();

    /** 마지막 FREE 룰렛이 24시간 이상 지난 활성 사용자 */
    List<Long> findUserSeqsRouletteReady();

    /** streak > 0 이고 오늘 체중 기록이 없는 사용자 */
    List<Long> findUserSeqsStreakAtRisk();
}
