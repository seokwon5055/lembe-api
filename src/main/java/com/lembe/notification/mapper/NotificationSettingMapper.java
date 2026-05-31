package com.lembe.notification.mapper;

import com.lembe.notification.domain.NotificationSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationSettingMapper {

    NotificationSetting findByUcode(String ucode);

    void upsert(NotificationSetting setting);
}
