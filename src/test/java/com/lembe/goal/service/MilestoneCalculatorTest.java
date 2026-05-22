package com.lembe.goal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MilestoneCalculatorTest {

    private MilestoneCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new MilestoneCalculator();
    }

    @Test
    @DisplayName("정상 케이스: 70→60 (10kg) → -3,-6,-9,-10(final) 총 4개")
    void normalCase_70to60() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("70"), bd("60"));

        assertThat(results).hasSize(4);

        assertResult(results.get(0), 1, "-3kg", "3.00", "67.00", false);
        assertResult(results.get(1), 2, "-6kg", "6.00", "64.00", false);
        assertResult(results.get(2), 3, "-9kg", "9.00", "61.00", false);
        assertResult(results.get(3), 4, "-10kg", "10.00", "60.00", true);
    }

    @Test
    @DisplayName("정확 배수: 70→61 (9kg) → -3,-6,-9(final) 총 3개")
    void exactMultiple_70to61() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("70"), bd("61"));

        assertThat(results).hasSize(3);
        assertResult(results.get(2), 3, "-9kg", "9.00", "61.00", true);
    }

    @Test
    @DisplayName("소량 목표 (<3kg): 70→68.5 → final 1개만")
    void smallTarget_under3kg() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("70"), bd("68.5"));

        assertThat(results).hasSize(1);
        assertResult(results.get(0), 1, "-1.5kg", "1.50", "68.50", true);
    }

    @Test
    @DisplayName("목표 차이 정확히 3kg: 70→67 → -3(final) 1개")
    void exactly3kg() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("70"), bd("67"));

        // step(3) is NOT < totalLoss(3), so goes straight to final
        assertThat(results).hasSize(1);
        assertResult(results.get(0), 1, "-3kg", "3.00", "67.00", true);
    }

    @Test
    @DisplayName("동일 체중: 결과 없음")
    void sameWeight() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("70"), bd("70"));

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("체중 증가 목표: 결과 없음")
    void weightGain() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("65"), bd("70"));

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("0.05 단위 반올림: 70→62.3 → -3kg 시 67.00")
    void roundTo05() {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("70"), bd("62.3"));

        // step 3 → 70 - 3 = 67.00 (already 0.05 aligned)
        assertThat(results.get(0).targetWeight()).isEqualByComparingTo(bd("67.00"));
    }

    @Test
    @DisplayName("재계산 startSeq: 마지막 달성 seq=2 이후 seq=3부터 시작")
    void reCalcWithStartSeq() {
        // 70kg 시작 → seq1(-3kg/67kg) 달성 후 목표 62kg으로 변경
        // baseWeight=67, targetWeight=62, startSeq=2
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd("67"), bd("62"), 2, null, null, null, null);

        // 67-62=5kg: step3(seq2, 64kg), then final 5kg(seq3, 62kg)
        assertThat(results).hasSize(2);
        assertThat(results.get(0).sequence()).isEqualTo(2);
        assertThat(results.get(1).sequence()).isEqualTo(3);
        assertResult(results.get(0), 2, "-3kg", "3.00", "64.00", false);
        assertResult(results.get(1), 3, "-5kg", "5.00", "62.00", true);
    }

    @Test
    @DisplayName("정밀 모드: 체지방/근육 비례 계산")
    void preciseMode_proportional() {
        // 70→60, 체지방 25%→20%, 근육 30kg→32kg
        List<MilestoneCalculator.MilestoneCalcResult> results = calculator.calculate(
                bd("70"), bd("60"), 1,
                bd("25.0"), bd("20.0"),
                bd("30.0"), bd("32.0")
        );

        // -3kg (30% 진행): 체지방 25 + (20-25)*0.3 = 23.5%, 근육 30 + (32-30)*0.3 = 30.6kg
        MilestoneCalculator.MilestoneCalcResult first = results.get(0);
        assertThat(first.targetBodyFatPct()).isEqualByComparingTo(bd("23.5"));
        assertThat(first.targetMuscleMassKg()).isEqualByComparingTo(bd("30.60"));

        // -10kg (100% 진행): 체지방 20%, 근육 32kg
        MilestoneCalculator.MilestoneCalcResult last = results.get(results.size() - 1);
        assertThat(last.targetBodyFatPct()).isEqualByComparingTo(bd("20.0"));
        assertThat(last.targetMuscleMassKg()).isEqualByComparingTo(bd("32.00"));
    }

    @ParameterizedTest(name = "start={0}, target={1} → {2}개 마일스톤")
    @CsvSource({
            "80, 50, 10",   // 30kg: -3,-6,-9,-12,-15,-18,-21,-24,-27,-30(final)
            "70, 67, 1",    // 3kg: final 1개
            "70, 67.1, 1",  // 2.9kg: final 1개
            "70, 64, 2",    // 6kg: -3,-6(final)
            "70, 58, 4",    // 12kg: -3,-6,-9,-12(final)
    })
    @DisplayName("다양한 감량 범위 케이스")
    void parameterizedCases(String start, String target, int expectedSize) {
        List<MilestoneCalculator.MilestoneCalcResult> results =
                calculator.calculate(bd(start), bd(target));
        assertThat(results).hasSize(expectedSize);
        assertThat(results.get(results.size() - 1).isFinal()).isTrue();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void assertResult(MilestoneCalculator.MilestoneCalcResult r,
                               int seq, String label, String loss, String weight, boolean isFinal) {
        assertThat(r.sequence()).isEqualTo(seq);
        assertThat(r.label()).isEqualTo(label);
        assertThat(r.lossKg()).isEqualByComparingTo(bd(loss));
        assertThat(r.targetWeight()).isEqualByComparingTo(bd(weight));
        assertThat(r.isFinal()).isEqualTo(isFinal);
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
