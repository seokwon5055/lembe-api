package com.lembe.notification.dto;

import com.lembe.notification.domain.NotificationSetting;

public record NotificationSettingResponse(
        boolean milestoneReady,
        boolean milestoneUnlocked,
        boolean rouletteReady,
        boolean streakWarning,
        boolean weightReminder
) {
    public static NotificationSettingResponse from(NotificationSetting s) {
        return new NotificationSettingResponse(
                s.isMilestoneReady(),
                s.isMilestoneUnlocked(),
                s.isRouletteReady(),
                s.isStreakWarning(),
                s.isWeightReminder()
        );
    }
}
