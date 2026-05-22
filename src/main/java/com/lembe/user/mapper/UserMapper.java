package com.lembe.user.mapper;

import com.lembe.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findById(Long userSeq);

    User findByEmail(String email);

    void insert(User user);

    void updateStreak(@Param("userSeq") Long userSeq,
                      @Param("streakCount") int streakCount,
                      @Param("lastStreakDt") java.time.LocalDate lastStreakDt);
}
