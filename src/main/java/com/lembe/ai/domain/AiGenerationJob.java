package com.lembe.ai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGenerationJob {

    private Long genSeq;
    private String ucode;
    private String jobType;          // INITIAL | MILESTONE | ADAPTIVE | FINAL_UPDATE
    private Long milestoneSeq;
    private Long inputPhotoSeq;      // nullable: 초기 생성 시 없음
    private Long resultPhotoSeq;     // nullable: 완료 전 없음
    private String status;           // PENDING | PROCESSING | COMPLETED | FAILED
    private String falRequestId;
    private String prompt;
    private int pointUsed;
    private BigDecimal costUsd;
    private boolean isFreeRetry;
    private int retryCount;
    private String errorMsg;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
}
