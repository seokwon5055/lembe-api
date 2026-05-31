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
            return issueTokens(existing.getUcode(), false, null);
        }

        // 신규 회원 — 가입 트랜잭션
        String ucode = registerNewUser(provider, userInfo);
        return issueTokens(ucode, true, userInfo.nickname());
    }

    private SocialUserInfo fetchUserInfo(String provider, String accessToken) {
        return switch (provider.toUpperCase()) {
            case "KAKAO" -> kakaoOAuthClient.fetchUserInfo(accessToken);
            case "APPLE" -> throw new LembeException(ErrorCode.INVALID_INPUT, "Apple 로그인은 준비 중입니다.");
            case "GOOGLE" -> throw new LembeException(ErrorCode.INVALID_INPUT, "Google 로그인은 준비 중입니다.");
            default -> throw new LembeException(ErrorCode.INVALID_INPUT, "지원하지 않는 provider: " + provider);
        };
    }

    private String registerNewUser(String provider, SocialUserInfo userInfo) {
        // 1. user INSERT — trigger가 ucode 자동 생성
        User user = User.builder()
                .email(userInfo.email())
                .nickname(userInfo.nickname())
                .profileImgUrl(userInfo.profileImageUrl())
                .build();
        userMapper.insert(user);
        // trigger가 server-side에서 ucode를 설정하므로 seq로 재조회
        user = userMapper.findById(user.getSeq());
        String ucode = user.getUcode();

        // 2. social_account INSERT
        SocialAccount socialAccount = SocialAccount.builder()
                .ucode(ucode)
                .provider(provider.toUpperCase())
                .providerId(userInfo.providerId())
                .email(userInfo.email())
                .build();
        socialAccountMapper.insert(socialAccount);

        // 3. user_profile INSERT (기본값)
        UserProfile userProfile = UserProfile.builder()
                .ucode(ucode)
                .notificationEnabled(true)
                .build();
        userProfileMapper.insert(userProfile);

        // 4. point_wallet INSERT (가입 보너스)
        PointWallet wallet = PointWallet.builder()
                .ucode(ucode)
                .balance(SIGNUP_BONUS_POINTS)
                .build();
        pointWalletMapper.insert(wallet);

        log.info("[SocialLogin] new user registered: ucode={}, provider={}", ucode, provider);
        return ucode;
    }

    private SocialLoginResponse issueTokens(String ucode, boolean isNewUser, String kakaoNickname) {
        return new SocialLoginResponse(
                jwtTokenProvider.createAccessToken(ucode),
                jwtTokenProvider.createRefreshToken(ucode),
                isNewUser,
                kakaoNickname
        );
    }
}
