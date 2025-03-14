package com.example.meetingmanagementproject.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {
    private static final String SECRET = "my-secret-key";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;  // 15분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일
    private final ConcurrentHashMap<String, Boolean> invalidTokens = new ConcurrentHashMap<>();

    public String generateAccessToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public String generateRefreshToken(Long userId) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public Long validateToken(String token) {
        try {
            if (invalidTokens.containsKey(token)) {
                return null;
            }
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
            return Long.parseLong(decodedJWT.getSubject());
        } catch (JWTVerificationException | NumberFormatException e) {
            return null;
        }
    }

    public void invalidateToken(String token) {
        invalidTokens.put(token, true);
    }
}