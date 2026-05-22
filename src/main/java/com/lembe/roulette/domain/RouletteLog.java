package com.lembe.roulette.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RouletteLog {
    private Long rouletteLogSeq;
    private Long userSeq;
    private String spinType;    // FREE | AD_BONUS
    private int pointsWon;
    private LocalDateTime spunAt;
}
