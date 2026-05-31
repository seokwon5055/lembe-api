package com.lembe.point.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.point.dto.RouletteResponse;
import com.lembe.roulette.domain.RouletteLog;
import com.lembe.roulette.mapper.RouletteMapper;
import com.lembe.user.domain.User;
import com.lembe.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouletteService {

    // 가중치 테이블: 합계 100
    private static final int[] WEIGHTS = {30, 40, 20, 9, 1};
    private static final int[] PRIZES  = { 1,  3,  5, 10, 30};

    private static final int AD_SPIN_DAILY_LIMIT = 3;
    private static final int RATE_LIMIT_TTL_SECS = 5;

    private final RouletteMapper rouletteMapper;
    private final PointService pointService;
    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public RouletteResponse spin(String ucode) {
        acquireRateLock(ucode);
        checkFreeSpinCooldown(ucode);
        return doSpin(ucode, "FREE");
    }

    @Transactional
    public RouletteResponse spinAd(String ucode) {
        acquireRateLock(ucode);
        int adCountToday = rouletteMapper.countAdBonusTodayByUserSeq(ucode);
        if (adCountToday >= AD_SPIN_DAILY_LIMIT) {
            throw new LembeException(ErrorCode.INVALID_INPUT,
                    "오늘 광고 보상 룰렛을 모두 사용했습니다. (일 " + AD_SPIN_DAILY_LIMIT + "회 제한)");
        }
        return doSpin(ucode, "AD_BONUS");
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private RouletteResponse doSpin(String ucode, String spinType) {
        int pointsWon = drawPrize();
        int streakBonus = calcStreakBonus(ucode);
        int totalEarned = pointsWon + streakBonus;

        int balanceAfter = pointService.add(ucode, totalEarned, "ROULETTE", spinType);

        LocalDateTime spunAt = LocalDateTime.now();
        rouletteMapper.insert(RouletteLog.builder()
                .ucode(ucode)
                .spinType(spinType)
                .pointsWon(totalEarned)
                .spunAt(spunAt)
                .build());

        LocalDateTime nextFreeAt = "FREE".equals(spinType) ? spunAt.plusHours(24) : null;
        int adRemaining = AD_SPIN_DAILY_LIMIT - rouletteMapper.countAdBonusTodayByUserSeq(ucode);

        log.info("[Roulette] user={} type={} won={}P bonus={}P total={}P balance={}",
                ucode, spinType, pointsWon, streakBonus, totalEarned, balanceAfter);

        return new RouletteResponse(pointsWon, streakBonus, totalEarned,
                balanceAfter, nextFreeAt, Math.max(0, adRemaining));
    }

    /** Redis rate lock — TTL 5초: 동일 사용자의 연속 중복 호출 방지 */
    private void acquireRateLock(String ucode) {
        String key = "rate:roulette:" + ucode;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(RATE_LIMIT_TTL_SECS));
        if (!Boolean.TRUE.equals(acquired)) {
            throw new LembeException(ErrorCode.INVALID_INPUT,
                    "요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
        }
    }

    /** FREE 스핀 24시간 쿨다운 체크 */
    private void checkFreeSpinCooldown(String ucode) {
        RouletteLog last = rouletteMapper.findLastFreeByUserSeq(ucode);
        if (last == null) return;

        LocalDateTime nextAvailable = last.getSpunAt().plusHours(24);
        if (LocalDateTime.now().isBefore(nextAvailable)) {
            throw new LembeException(ErrorCode.INVALID_INPUT,
                    "다음 무료 룰렛은 " + nextAvailable + " 이후에 가능합니다.");
        }
    }

    /** 가중치 기반 포인트 추첨: roll 1~100 → PRIZES */
    private int drawPrize() {
        int roll = ThreadLocalRandom.current().nextInt(100) + 1;
        int cumulative = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            cumulative += WEIGHTS[i];
            if (roll <= cumulative) return PRIZES[i];
        }
        return PRIZES[PRIZES.length - 1];
    }

    /** Streak 보너스: 30일≥ → +3P, 7일≥ → +1P */
    private int calcStreakBonus(String ucode) {
        User user = userMapper.findByUcode(ucode);
        if (user == null) return 0;
        int streak = user.getStreakCount();
        if (streak >= 30) return 3;
        if (streak >= 7)  return 1;
        return 0;
    }
}
