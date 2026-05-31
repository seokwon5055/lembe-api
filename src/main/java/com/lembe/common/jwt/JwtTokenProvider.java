package com.lembe.common.jwt;

import com.lembe.common.config.JwtProperties;
import com.lembe.common.exception.ErrorCode;
import com.lembe.common.exception.LembeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "ACCESS";
    private static final String TYPE_REFRESH = "REFRESH";

    private final JwtProperties jwtProperties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String createAccessToken(String ucode) {
        return buildToken(ucode, TYPE_ACCESS, jwtProperties.getAccessTokenExpiry());
    }

    public String createRefreshToken(String ucode) {
        return buildToken(ucode, TYPE_REFRESH, jwtProperties.getRefreshTokenExpiry());
    }

    public String getUcode(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateAccessToken(String token) {
        return validateTokenType(token, TYPE_ACCESS);
    }

    public boolean validateRefreshToken(String token) {
        return validateTokenType(token, TYPE_REFRESH);
    }

    public String getUcodeFromRefreshToken(String token) {
        Claims claims = parseClaims(token);
        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new LembeException(ErrorCode.INVALID_TOKEN);
        }
        return claims.getSubject();
    }

    private String buildToken(String ucode, String type, long expiryMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(ucode)
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiryMs))
                .signWith(key)
                .compact();
    }

    private boolean validateTokenType(String token, String expectedType) {
        try {
            Claims claims = parseClaims(token);
            return expectedType.equals(claims.get(CLAIM_TYPE, String.class));
        } catch (ExpiredJwtException e) {
            throw new LembeException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new LembeException(ErrorCode.INVALID_TOKEN);
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new LembeException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new LembeException(ErrorCode.INVALID_TOKEN);
        }
    }
}
