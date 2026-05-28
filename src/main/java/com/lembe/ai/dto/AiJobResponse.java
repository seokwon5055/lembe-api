package com.lembe.ai.dto;

import com.lembe.ai.domain.AiGenerationJob;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AiJobResponse(
        Long genSeq,
        String jobType,
        Long milestoneSeq,
        String status,
        String resultPhotoUrl,
        BigDecimal costUsd,
        int retryCount,
        String errorMsg,
        LocalDateTime requestedAt,
        LocalDateTime completedAt
) {
    public static AiJobResponse from(AiGenerationJob job, String resultPhotoUrl) {
        return new AiJobResponse(
                job.getGenSeq(),
                job.getJobType(),
                job.getMilestoneSeq(),
                job.getStatus(),
                resultPhotoUrl,
                job.getCostUsd(),
                job.getRetryCount(),
                job.getErrorMsg(),
                job.getRequestedAt(),
                job.getCompletedAt()
        );
    }
}
