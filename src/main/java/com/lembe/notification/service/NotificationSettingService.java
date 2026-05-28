package com.lembe.notification.service;

import com.lembe.notification.domain.NotificationSetting;
import com.lembe.notification.dto.NotificationSettingResponse;
import com.lembe.notification.dto.UpdateNotificationSettingRequest;
import com.lembe.notification.mapper.NotificationSettingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingMapper notificationSettingMapper;

    @Transactional(readOnly = true)
    public NotificationSettingResponse getSettings(Long userSeq) {
        NotificationSetting setting = notificationSettingMapper.findByUserSeq(userSeq);
        if (setting == null) {
            setting = NotificationSetting.defaultFor(userSeq);
        }
        return NotificationSettingResponse.from(setting);
    }

    @Transactional
    public NotificationSettingResponse updateSettings(Long userSeq, UpdateNotificationSettingRequest req) {
        NotificationSetting setting = NotificationSetting.builder()
                .userSeq(userSeq)
                .milestoneReady(req.milestoneReady())
                .milestoneUnlocked(req.milestoneUnlocked())
                .rouletteReady(req.rouletteReady())
                .streakWarning(req.streakWarning())
                .weightReminder(req.weightReminder())
                .build();
        notificationSettingMapper.upsert(setting);
        return NotificationSettingResponse.from(setting);
    }
}
