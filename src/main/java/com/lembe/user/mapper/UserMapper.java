package com.lembe.user.mapper;

import com.lembe.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findById(@Param("seq") Long seq);

    User findByUcode(@Param("ucode") String ucode);

    User findByEmail(String email);

    void insert(User user);

    void updateStreak(@Param("ucode") String ucode,
                      @Param("streakCount") int streakCount,
                      @Param("lastStreakDt") java.time.LocalDate lastStreakDt);

    boolean existsByNickname(@Param("nickname") String nickname);

    void updateNickname(@Param("ucode") String ucode, @Param("nickname") String nickname);
}
