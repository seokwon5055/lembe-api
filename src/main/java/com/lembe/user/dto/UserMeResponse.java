package com.lembe.user.dto;

import com.lembe.user.domain.User;

public record UserMeResponse(
        Long seq,
        String ucode,
        String email,
        String nickname,
        String profileImgUrl,
        int streakCount,
        int pointBalance
) {
    public static UserMeResponse of(User user, int pointBalance) {
        return new UserMeResponse(
                user.getSeq(),
                user.getUcode(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImgUrl(),
                user.getStreakCount(),
                pointBalance
        );
    }
}
