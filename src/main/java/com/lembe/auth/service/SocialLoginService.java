package com.lembe.auth.service;

import com.lembe.auth.dto.SocialLoginResponse;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.common.jwt.JwtTokenProvider;
import com.lembe.user.domain.PointWallet;
import com.lembe.user.domain.SocialAccount;
import com.lembe.user.domain.User;
import com.lembe.user.domain.UserProfile;
import com.lembe.user.mapper.PointWalletMapper;
import com.lembe.user.mapper.SocialAccountMapper;
import com.lembe.user.mapper.UserMapper;
import com.lembe.user.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private static final int SIGNUP_BONUS_POINTS = 10;

    private final KakaoOAuthClient kakaoOAuthClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final SocialAccountMapper socialAccountMapper;
    private final UserProfileMapper userProfileMapper;
    private final PointWalletMapper pointWalletMapper;

    @Transactional
    public SocialLoginResponse login(String provider, String accessToken) {
        SocialUserInfo userInfo = fetchUserInfo(provider, accessToken);

        SocialAccount existing = socialAccountMapper.findByProviderAndProviderId(provider, userInfo.providerId());

        if (existing != null) {
            // 기존 회원 — 로그인
            return issueTokens(existing.getUserSeq(), false);
        }

        // 신규 회원 — 가입 트랜잭션
        Long userSeq = registerNewUser(provider, userInfo);
        return issueTokens(userSeq, true);
    }

    private SocialUserInfo fetchUserInfo(String provider, String accessToken) {
        return switch (provider.toUpperCase()) {
            case "KAKAO" -> kakaoOAuthClient.fetchUserInfo(accessToken);
            case "APPLE" -> throw new LembeException(ErrorCode.INVALID_INPUT, "Apple 로그인은 준비 중입니다.");
            case "GOOGLE" -> throw new LembeException(ErrorCode.INVALID_INPUT, "Google 로그인은 준비 중입니다.");
            default -> throw new LembeException(ErrorCode.INVALID_INPUT, "지원하지 않는 provider: " + provider);
        };
    }

    private Long registerNewUser(String provider, SocialUserInfo userInfo) {
        // 1. tb_user INSERT
        User user = User.builder()
                .email(userInfo.email())
                .nickname(userInfo.nickname())
                .profileImgUrl(userInfo.profileImageUrl())
                .build();
        userMapper.insert(user);
        Long userSeq = user.getUserSeq();

        // 2. tb_social_account INSERT
        SocialAccount socialAccount = SocialAccount.builder()
                .userSeq(userSeq)
                .provider(provider.toUpperCase())
                .providerId(userInfo.providerId())
                .email(userInfo.email())
                .build();
        socialAccountMapper.insert(socialAccount);

        // 3. tb_user_profile INSERT (기본값)
        UserProfile userProfile = UserProfile.builder()
                .userSeq(userSeq)
                .notificationEnabled(true)
                .build();
        userProfileMapper.insert(userProfile);

        // 4. tb_point_wallet INSERT (가입 보너스)
        PointWallet wallet = PointWallet.builder()
                .userSeq(userSeq)
                .balance(SIGNUP_BONUS_POINTS)
                .build();
        pointWalletMapper.insert(wallet);

        log.info("[SocialLogin] new user registered: userSeq={}, provider={}", userSeq, provider);
        return userSeq;
    }

    private SocialLoginResponse issueTokens(Long userSeq, boolean isNewUser) {
        return new SocialLoginResponse(
                jwtTokenProvider.createAccessToken(userSeq),
                jwtTokenProvider.createRefreshToken(userSeq),
                isNewUser
        );
    }
}
