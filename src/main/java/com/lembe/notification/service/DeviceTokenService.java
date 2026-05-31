package com.lembe.notification.service;

import com.lembe.notification.domain.DeviceToken;
import com.lembe.notification.dto.DeviceTokenRequest;
import com.lembe.notification.mapper.DeviceTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenMapper deviceTokenMapper;

    @Transactional
    public void registerToken(String ucode, DeviceTokenRequest req) {
        DeviceToken token = DeviceToken.builder()
                .ucode(ucode)
                .fcmToken(req.fcmToken())
                .platform(req.platform())
                .build();
        deviceTokenMapper.upsert(token);
    }
}
