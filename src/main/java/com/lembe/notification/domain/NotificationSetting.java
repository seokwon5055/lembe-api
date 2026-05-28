package com.lembe.notification.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationSetting {
    private Long settingSeq;
    private Long userSeq;
    private boolean milestoneReady;
    private boolean milestoneUnlocked;
    private boolean rouletteReady;
    private boolean streakWarning;
    private boolean weightReminder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationSetting defaultFor(Long userSeq) {
        return NotificationSetting.builder()
                .userSeq(userSeq)
                .milestoneReady(true)
                .milestoneUnlocked(true)
                .rouletteReady(true)
                .streakWarning(true)
                .weightReminder(true)
                .build();
    }

    public boolean isEnabled(NotificationType type) {
        return switch (type) {
            case MILESTONE_READY     -> milestoneReady;
            case MILESTONE_UNLOCKED  -> milestoneUnlocked;
            case ROULETTE_READY      -> rouletteReady;
            case STREAK_WARNING      -> streakWarning;
            case WEIGHT_REMINDER     -> weightReminder;
        };
    }
}
