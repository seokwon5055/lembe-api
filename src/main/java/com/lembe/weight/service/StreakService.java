package com.lembe.weight.service;

import com.lembe.user.domain.User;
import com.lembe.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserMapper userMapper;

    public record StreakResult(int streakCount, boolean updated) {}

    /**
     * 체중 기록 날짜 기준으로 streak 업데이트.
     * - 오늘 이미 기록 → no-op
     * - 어제 기록 → streak + 1
     * - 그 외 → streak = 1 (리셋)
     */
    public StreakResult update(String ucode, LocalDate recordDate) {
        User user = userMapper.findByUcode(ucode);
        if (user == null) return new StreakResult(1, false);

        LocalDate lastStreakDt = user.getLastStreakDt();

        // 같은 날 중복 기록 → 무시
        if (recordDate.equals(lastStreakDt)) {
            return new StreakResult(user.getStreakCount(), false);
        }

        int newStreak;
        if (lastStreakDt != null && recordDate.equals(lastStreakDt.plusDays(1))) {
            // 연속 기록
            newStreak = user.getStreakCount() + 1;
        } else {
            // 처음이거나 끊김
            newStreak = 1;
        }

        userMapper.updateStreak(ucode, newStreak, recordDate);
        return new StreakResult(newStreak, true);
    }
}
