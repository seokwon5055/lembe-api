package com.lembe.goal.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MilestoneCalculator {

    private static final BigDecimal INTERVAL = BigDecimal.valueOf(3);

    public record MilestoneCalcResult(
            int sequence,
            String label,
            BigDecimal lossKg,
            BigDecimal targetWeight,
            BigDecimal targetBodyFatPct,
            BigDecimal targetMuscleMassKg,
            boolean isFinal
    ) {}

    /**
     * @param startWeight      기준 체중 (시작점 또는 마지막 달성 체중)
     * @param targetWeight     최종 목표 체중
     * @param startSeq         시작 sequence 번호 (신규=1, 재계산=마지막달성seq+1)
     * @param startBodyFatPct  정밀 모드 시작 체지방 (nullable)
     * @param targetBodyFatPct 정밀 모드 목표 체지방 (nullable)
     * @param startMuscle      정밀 모드 시작 근육량 (nullable)
     * @param targetMuscle     정밀 모드 목표 근육량 (nullable)
     */
    public List<MilestoneCalcResult> calculate(
            BigDecimal startWeight,
            BigDecimal targetWeight,
            int startSeq,
            BigDecimal startBodyFatPct,
            BigDecimal targetBodyFatPct,
            BigDecimal startMuscle,
            BigDecimal targetMuscle) {

        BigDecimal totalLoss = startWeight.subtract(targetWeight);
        if (totalLoss.compareTo(BigDecimal.ZERO) <= 0) {
            return List.of();
        }

        boolean isPrecise = startBodyFatPct != null && targetBodyFatPct != null;
        List<MilestoneCalcResult> results = new ArrayList<>();

        if (totalLoss.compareTo(INTERVAL) < 0) {
            // 목표 차이 < 3kg → 최종 마일스톤 1개만
            results.add(build(startSeq, totalLoss, totalLoss, targetWeight,
                    isPrecise, startBodyFatPct, targetBodyFatPct, startMuscle, targetMuscle, totalLoss, true));
        } else {
            int seq = startSeq;
            BigDecimal step = INTERVAL;

            while (step.compareTo(totalLoss) < 0) {
                BigDecimal weight = roundTo05(startWeight.subtract(step));
                results.add(build(seq++, step, totalLoss, weight,
                        isPrecise, startBodyFatPct, targetBodyFatPct, startMuscle, targetMuscle, totalLoss, false));
                step = step.add(INTERVAL);
            }

            // 마지막 = 목표 체중
            results.add(build(seq, totalLoss, totalLoss, targetWeight,
                    isPrecise, startBodyFatPct, targetBodyFatPct, startMuscle, targetMuscle, totalLoss, true));
        }

        return results;
    }

    /** startSeq = 1 인 단순 호출 */
    public List<MilestoneCalcResult> calculate(BigDecimal startWeight, BigDecimal targetWeight) {
        return calculate(startWeight, targetWeight, 1, null, null, null, null);
    }

    private MilestoneCalcResult build(
            int sequence,
            BigDecimal loss,
            BigDecimal totalLoss,
            BigDecimal milestoneWeight,
            boolean isPrecise,
            BigDecimal startBodyFat,
            BigDecimal targetBodyFat,
            BigDecimal startMuscle,
            BigDecimal targetMuscle,
            BigDecimal fullTotalLoss,
            boolean isFinal) {

        BigDecimal bodyFatAtPoint = null;
        BigDecimal muscleAtPoint = null;

        if (isPrecise) {
            BigDecimal progress = loss.divide(fullTotalLoss, 6, RoundingMode.HALF_UP);
            bodyFatAtPoint = interpolate(startBodyFat, targetBodyFat, progress, 1);
            if (startMuscle != null && targetMuscle != null) {
                muscleAtPoint = interpolate(startMuscle, targetMuscle, progress, 2);
            }
        }

        return new MilestoneCalcResult(
                sequence,
                formatLabel(loss),
                loss.setScale(2, RoundingMode.HALF_UP),
                milestoneWeight,
                bodyFatAtPoint,
                muscleAtPoint,
                isFinal
        );
    }

    private BigDecimal interpolate(BigDecimal start, BigDecimal end, BigDecimal progress, int scale) {
        return start.add(end.subtract(start).multiply(progress))
                .setScale(scale, RoundingMode.HALF_UP);
    }

    // Math.round(x * 20) / 20 → 0.05 단위 반올림
    private BigDecimal roundTo05(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(20))
                .setScale(0, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP);
    }

    private String formatLabel(BigDecimal loss) {
        BigDecimal stripped = loss.stripTrailingZeros();
        String plain = (stripped.scale() <= 0)
                ? loss.setScale(0, RoundingMode.UNNECESSARY).toPlainString()
                : loss.setScale(1, RoundingMode.HALF_UP).toPlainString();
        return "-" + plain + "kg";
    }
}
