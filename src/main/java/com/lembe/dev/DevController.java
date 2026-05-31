package com.lembe.dev;

import com.lembe.common.dto.ApiResponse;
import com.lembe.common.jwt.JwtTokenProvider;
import com.lembe.user.domain.PointWallet;
import com.lembe.user.domain.User;
import com.lembe.user.mapper.PointWalletMapper;
import com.lembe.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequestMapping("/api/v1/dev")
@RequiredArgsConstructor
public class DevController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final PointWalletMapper pointWalletMapper;

    /**
     * 로컬 테스트용 더미 로그인.
     * nickname 으로 기존 유저 조회 → 없으면 생성 → access token 반환.
     */
    @PostMapping("/login")
    @Transactional
    public ApiResponse<DevLoginResponse> login(@RequestBody DevLoginRequest req) {
        String nickname = req.nickname() != null ? req.nickname() : "tester";

        String email = "dev+" + nickname + "@lembe.local";
        User user = userMapper.findByEmail(email);
        if (user == null) {
            userMapper.insert(User.builder()
                    .email(email)
                    .nickname(nickname)
                    .build());
            // trigger가 server-side에서 ucode 설정하므로 seq로 재조회
            user = userMapper.findById(userMapper.findByEmail(email).getSeq());

            pointWalletMapper.insert(PointWallet.builder()
                    .ucode(user.getUcode())
                    .balance(100)
                    .build());
        }

        String token = jwtTokenProvider.createAccessToken(user.getUcode());
        return ApiResponse.ok(new DevLoginResponse(user.getUcode(), token));
    }

    public record DevLoginResponse(String ucode, String accessToken) {}
}
