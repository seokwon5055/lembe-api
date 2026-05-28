package com.lembe.notification.service;

import com.lembe.notification.domain.NotificationType;
import com.lembe.notification.mapper.SchedulerQueryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final SchedulerQueryMapper schedulerQueryMapper;
    private final NotificationService notificationService;

    /** 매일 10:00 — 무료 룰렛 가능 알림 */
    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void sendRouletteReady() {
        List<Long> targets = schedulerQueryMapper.findUserSeqsRouletteReady();
        log.info("[Scheduler] ROULETTE_READY targets={}", targets.size());
        targets.forEach(userSeq ->
                notificationService.sendToUser(userSeq, NotificationType.ROULETTE_READY));
    }

    /** 매일 20:00 — 체중 미기록 사용자 리마인더 */
    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Seoul")
    public void sendWeightReminder() {
        List<Long> targets = schedulerQueryMapper.findUserSeqsWithNoWeightLogToday();
        log.info("[Scheduler] WEIGHT_REMINDER targets={}", targets.size());
        targets.forEach(userSeq ->
                notificationService.sendToUser(userSeq, NotificationType.WEIGHT_REMINDER));
    }

    /** 매일 21:00 — streak 끊길 위험 경고 */
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void sendStreakWarning() {
        List<Long> targets = schedulerQueryMapper.findUserSeqsStreakAtRisk();
        log.info("[Scheduler] STREAK_WARNING targets={}", targets.size());
        targets.forEach(userSeq ->
                notificationService.sendToUser(userSeq, NotificationType.STREAK_WARNING));
    }
}
