package com.lembe.user.mapper;

import com.lembe.user.domain.SocialAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SocialAccountMapper {

    SocialAccount findByProviderAndProviderId(@Param("provider") String provider,
                                              @Param("providerId") String providerId);

    void insert(SocialAccount socialAccount);
}
