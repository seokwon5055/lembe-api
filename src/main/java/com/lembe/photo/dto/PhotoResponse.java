package com.lembe.photo.dto;

import com.lembe.photo.domain.Photo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PhotoResponse(
        Long photoSeq,
        String photoType,
        String url,
        BigDecimal weightKg,
        boolean isMain,
        int version,
        Long parentPhotoSeq,
        Long milestoneSeq,
        LocalDateTime takenAt,
        LocalDateTime createdAt
) {
    public static PhotoResponse from(Photo p) {
        return new PhotoResponse(
                p.getPhotoSeq(),
                p.getPhotoType(),
                p.getCdnUrl(),
                p.getWeightKg(),
                p.isMain(),
                p.getVersion(),
                p.getParentPhotoSeq(),
                p.getMilestoneSeq(),
                p.getTakenAt(),
                p.getCreatedAt()
        );
    }
}
