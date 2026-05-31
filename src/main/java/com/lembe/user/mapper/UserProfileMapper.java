package com.lembe.user.mapper;

import com.lembe.user.domain.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper {

    void insert(UserProfile userProfile);

    UserProfile findByUcode(String ucode);
}
