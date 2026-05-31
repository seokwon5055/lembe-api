package com.lembe.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting {
    private Long settingSeq;
    private String ucode;
    private boolean milestoneReady;
    private boolean milestoneUnlocked;
    private boolean rouletteReady;
    private boolean streakWarning;
    private boolean weightReminder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationSetting defaultFor(String ucode) {
        return NotificationSetting.builder()
                .ucode(ucode)
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
