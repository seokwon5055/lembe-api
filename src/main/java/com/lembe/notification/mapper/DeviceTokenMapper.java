package com.lembe.notification.mapper;

import com.lembe.notification.domain.DeviceToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceTokenMapper {

    void upsert(DeviceToken token);

    List<DeviceToken> findActiveByUserSeq(Long userSeq);

    void deactivate(@Param("fcmToken") String fcmToken);
}
