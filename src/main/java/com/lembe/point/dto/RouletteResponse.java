package com.lembe.point.dto;

import java.time.LocalDateTime;

public record RouletteResponse(
        int pointsWon,
        int streakBonus,
        int totalEarned,
        int balanceAfter,
        LocalDateTime nextFreeAvailableAt,  // FREE 스핀의 다음 가능 시각
        int adSpinsRemainingToday
) {}
