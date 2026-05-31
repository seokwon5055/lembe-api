package com.lembe.notification.service;

import com.lembe.notification.domain.DeviceToken;
import com.lembe.notification.domain.NotificationSetting;
import com.lembe.notification.domain.NotificationType;
import com.lembe.notification.mapper.DeviceTokenMapper;
import com.lembe.notification.mapper.NotificationSettingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 푸시 알림 발송 서비스.
 *
 * sendToUser() 는 @Async로 실행되므로 호출자 트랜잭션과 별개 스레드에서 동작합니다.
 * 실제 FCM 연동 후 트랜잭션 커밋 이후 발송이 필요하다면
 * TransactionSynchronizationManager.registerSynchronization(afterCommit) 패턴 적용 필요.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenMapper deviceTokenMapper;
    private final NotificationSettingMapper notificationSettingMapper;
    private final FcmService fcmService;

    @Async("notificationExecutor")
    public void sendToUser(String ucode, NotificationType type) {
        sendToUser(ucode, type, Map.of());
    }

    @Async("notificationExecutor")
    public void sendToUser(String ucode, NotificationType type, Map<String, String> data) {
        if (!isEnabled(ucode, type)) {
            log.debug("[Notification] skip user={} type={} (disabled)", ucode, type);
            return;
        }

        List<DeviceToken> tokens = deviceTokenMapper.findActiveByUcode(ucode);
        if (tokens.isEmpty()) {
            log.debug("[Notification] skip user={} type={} (no device token)", ucode, type);
            return;
        }

        for (DeviceToken token : tokens) {
            boolean success = fcmService.send(token.getFcmToken(), type, data);
            if (!success) {
                log.warn("[Notification] invalid token deactivated: user={} token={}",
                        ucode, token.getFcmToken());
                deviceTokenMapper.deactivate(token.getFcmToken());
            }
        }
    }

    private boolean isEnabled(String ucode, NotificationType type) {
        NotificationSetting setting = notificationSettingMapper.findByUcode(ucode);
        if (setting == null) {
            return true;  // 설정 없으면 기본값 전체 허용
        }
        return setting.isEnabled(type);
    }
}
