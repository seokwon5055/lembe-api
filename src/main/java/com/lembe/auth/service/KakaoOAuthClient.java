package com.lembe.auth.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private static final String KAKAO_USER_ME_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;

    public SocialUserInfo fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_USER_ME_URL, HttpMethod.GET, entity, Map.class);

            Map<?, ?> body = response.getBody();
            if (body == null) {
                throw new LembeException(ErrorCode.INVALID_TOKEN, "카카오 응답이 비어있습니다.");
            }

            String providerId = String.valueOf(body.get("id"));
            Map<?, ?> kakaoAccount = (Map<?, ?>) body.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<?, ?> profile = kakaoAccount != null ? (Map<?, ?>) kakaoAccount.get("profile") : null;
            String nickname = profile != null ? (String) profile.get("nickname") : "lembe_" + providerId;
            String profileImageUrl = profile != null ? (String) profile.get("profile_image_url") : null;

            return new SocialUserInfo(providerId, email, nickname, profileImageUrl);

        } catch (HttpClientErrorException e) {
            log.warn("[Kakao] accessToken invalid: {}", e.getStatusCode());
            throw new LembeException(ErrorCode.INVALID_TOKEN, "카카오 토큰이 유효하지 않습니다.");
        }
    }
}
