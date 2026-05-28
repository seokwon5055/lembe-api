package com.lembe.notification.service;

import com.lembe.notification.domain.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * FCM 발송 서비스 (현재 mock — 로그만 출력)
 *
 * TODO: FCM 서버키 발급 후 아래 절차로 실제 연결
 *   1. build.gradle: implementation 'com.google.firebase:firebase-admin:9.x'
 *   2. application.yml: fcm.service-account-key-path 또는 FCM_SERVER_KEY 환경변수 설정
 *   3. FcmConfig.java: FirebaseApp 초기화
 *   4. 이 메서드에서 FirebaseMessaging.getInstance().send(...) 호출
 *   5. InvalidRegistrationException 수신 시 → 호출부에서 token deactivate 처리
 */
@Slf4j
@Service
public class FcmService {

    /**
     * @return true=발송 성공, false=토큰 무효(deactivate 필요)
     */
    public boolean send(String fcmToken, NotificationType type, Map<String, String> data) {
        // mock: 실제 FCM 호출 대신 로그만 기록
        log.info("[FCM-MOCK] send token={} type={} title='{}' body='{}' data={}",
                fcmToken, type.name(), type.getTitle(), type.getBody(), data);
        return true;
    }
}
