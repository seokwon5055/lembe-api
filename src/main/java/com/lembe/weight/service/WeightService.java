package com.lembe.weight.service;

import com.lembe.goal.domain.Goal;
import com.lembe.goal.domain.Milestone;
import com.lembe.goal.mapper.GoalMapper;
import com.lembe.goal.mapper.MilestoneMapper;
import com.lembe.point.domain.PointLog;
import com.lembe.point.mapper.PointLogMapper;
import com.lembe.user.domain.PointWallet;
import com.lembe.user.mapper.PointWalletMapper;
import com.lembe.weight.domain.WeightRecord;
import com.lembe.weight.dto.WeightHistoryResponse;
import com.lembe.weight.dto.WeightRecordRequest;
import com.lembe.weight.dto.WeightRecordResponse;
import com.lembe.weight.mapper.WeightMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeightService {

    private static final int WEIGHT_LOG_POINTS = 5;

    private final WeightMapper weightMapper;
    private final StreakService streakService;
    private final GoalMapper goalMapper;
    private final MilestoneMapper milestoneMapper;
    private final PointWalletMapper pointWalletMapper;
    private final PointLogMapper pointLogMapper;

    @Transactional
    public WeightRecordResponse record(Long userSeq, WeightRecordRequest req) {
        // 1. 체중 기록
        WeightRecord record = WeightRecord.builder()
                .userSeq(userSeq)
                .weightKg(req.weightKg())
                .bodyFatPct(req.bodyFatPct())
                .muscleMassKg(req.muscleMassKg())
                .source(req.source())
                .loggedAt(req.loggedAt())
                .build();
        weightMapper.insert(record);

        // 2. Streak 업데이트
        StreakService.StreakResult streak = streakService.update(userSeq, req.loggedAt());

        // 3. 마일스톤 도달 체크
        List<Long> readyMilestones = checkMilestones(userSeq, req.weightKg());

        // 4. 포인트 적립 (+5P) + 로그
        int newBalance = earnPoints(userSeq, WEIGHT_LOG_POINTS, "WEIGHT_LOG",
                String.valueOf(record.getLogSeq()));

        log.debug("[Weight] user={} weight={} streak={} +{}P balance={}",
                userSeq, req.weightKg(), streak.streakCount(), WEIGHT_LOG_POINTS, newBalance);

        return WeightRecordResponse.of(record, streak.streakCount(), WEIGHT_LOG_POINTS, readyMilestones);
    }

    @Transactional(readOnly = true)
    public WeightHistoryResponse getHistory(Long userSeq, int days) {
        LocalDate fromDate = (days <= 0) ? LocalDate.of(2000, 1, 1) : LocalDate.now().minusDays(days);
        List<WeightRecord> records = weightMapper.findHistory(userSeq, fromDate);

        if (records.isEmpty()) {
            return new WeightHistoryResponse(List.of(), null, null, null, days);
        }

        var points = records.stream().map(WeightHistoryResponse.WeightPoint::from).toList();
        var start = records.get(0).getWeightKg();
        var current = records.get(records.size() - 1).getWeightKg();
        var totalLoss = start.subtract(current);

        return new WeightHistoryResponse(points, start, current, totalLoss, days);
    }

    @Transactional
    public void syncHealthData(Long userSeq, List<WeightRecordRequest> records) {
        // 헬스케어 동기화: 날짜별 중복 제거 후 INSERT
        for (WeightRecordRequest req : records) {
            WeightRecord existing = weightMapper.findByUserAndDate(userSeq, req.loggedAt());
            if (existing == null) {
                record(userSeq, req);
            }
        }
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private List<Long> checkMilestones(Long userSeq, java.math.BigDecimal currentWeight) {
        Goal goal = goalMapper.findActiveByUserSeq(userSeq);
        if (goal == null) return Collections.emptyList();

        List<Milestone> reached = milestoneMapper.findReachedLocked(goal.getGoalSeq(), currentWeight);
        reached.forEach(m -> milestoneMapper.markReadyToUnlock(m.getMilestoneSeq()));
        return reached.stream().map(Milestone::getMilestoneSeq).toList();
    }

    private int earnPoints(Long userSeq, int delta, String reason, String refId) {
        pointWalletMapper.updateBalance(userSeq, delta);
        PointWallet wallet = pointWalletMapper.findByUserSeq(userSeq);
        int balanceAfter = (wallet != null) ? wallet.getBalance() : delta;

        pointLogMapper.insert(PointLog.builder()
                .userSeq(userSeq)
                .delta(delta)
                .balanceAfter(balanceAfter)
                .reason(reason)
                .refId(refId)
                .build());
        return balanceAfter;
    }
}
