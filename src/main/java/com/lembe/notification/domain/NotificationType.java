package com.lembe.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    MILESTONE_READY(
            "목표 체중 달성!",
            "마일스톤에 도달했어요. 포인트로 AI 사진을 잠금 해제해보세요!"
    ),
    MILESTONE_UNLOCKED(
            "AI 변신 사진 공개!",
            "새 AI 사진이 공개됐어요. 지금 확인해보세요."
    ),
    ROULETTE_READY(
            "무료 룰렛 준비 완료!",
            "지금 룰렛을 돌려서 포인트를 받아가세요."
    ),
    STREAK_WARNING(
            "오늘 체중 기록을 잊으셨나요?",
            "Streak이 끊기기 전에 체중을 기록해주세요!"
    ),
    WEIGHT_REMINDER(
            "체중 기록 시간이에요!",
            "오늘의 체중을 기록하고 5P를 받아가세요."
    );

    private final String title;
    private final String body;
}
