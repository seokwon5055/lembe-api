package com.lembe.roulette.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouletteLog {
    private Long rouletteLogSeq;
    private String ucode;
    private String spinType;    // FREE | AD_BONUS
    private int pointsWon;
    private LocalDateTime spunAt;
}
