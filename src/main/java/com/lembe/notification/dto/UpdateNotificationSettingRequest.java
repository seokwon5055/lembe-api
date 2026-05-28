package com.lembe.notification.dto;

public record UpdateNotificationSettingRequest(
        boolean milestoneReady,
        boolean milestoneUnlocked,
        boolean rouletteReady,
        boolean streakWarning,
        boolean weightReminder
) {}
