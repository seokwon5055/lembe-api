package com.lembe.user.service;

import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import com.lembe.user.domain.PointWallet;
import com.lembe.user.domain.User;
import com.lembe.user.dto.UserMeResponse;
import com.lembe.user.mapper.PointWalletMapper;
import com.lembe.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PointWalletMapper pointWalletMapper;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long userSeq) {
        User user = userMapper.findById(userSeq);
        if (user == null) {
            throw new LembeException(ErrorCode.USER_NOT_FOUND);
        }

        PointWallet wallet = pointWalletMapper.findByUserSeq(userSeq);
        int balance = wallet != null ? wallet.getBalance() : 0;

        return UserMeResponse.of(user, balance);
    }
}
