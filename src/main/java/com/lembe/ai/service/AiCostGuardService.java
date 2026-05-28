package com.lembe.ai.service;

import com.lembe.ai.mapper.AiGenerationMapper;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AiCostGuardService {

    private static final int USER_DAILY_LIMIT = 10;
    private static final BigDecimal GLOBAL_DAILY_LIMIT_USD = new BigDecimal("30.00");

    private final AiGenerationMapper aiGenerationMapper;

    @Transactional(readOnly = true)
    public void checkUserLimit(Long userSeq) {
        int count = aiGenerationMapper.countTodayByUserSeq(userSeq);
        if (count >= USER_DAILY_LIMIT) {
            throw new LembeException(ErrorCode.AI_DAILY_LIMIT_EXCEEDED);
        }
    }

    @Transactional(readOnly = true)
    public void checkGlobalLimit() {
        BigDecimal totalCost = aiGenerationMapper.sumTodayCostUsd();
        if (totalCost.compareTo(GLOBAL_DAILY_LIMIT_USD) >= 0) {
            throw new LembeException(ErrorCode.AI_GLOBAL_LIMIT_EXCEEDED);
        }
    }
}
