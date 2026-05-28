package com.lembe.photo.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Photo {

    private Long photoSeq;
    private Long userSeq;
    private Long milestoneSeq;
    private String photoType;       // CURRENT | AI_GENERATED | PROGRESS
    private String storageKey;      // 상대 경로 (삭제용)
    private String cdnUrl;          // 접근 URL
    private BigDecimal weightKg;
    private boolean isMain;
    private int version;
    private Long parentPhotoSeq;
    private boolean isDeleted;
    private LocalDateTime takenAt;
    private LocalDateTime createdAt;
}
