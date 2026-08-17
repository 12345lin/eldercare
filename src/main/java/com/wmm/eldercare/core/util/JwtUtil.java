package com.wmm.eldercare.core.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;                          // JWT 签名密钥

    @Value("${jwt.access-token-expire-hours}")
    private long accessTokenExpireHours;            // accessToken 过期时间（小时）

    @Value("${jwt.refresh-token-expire-days}")
    private long refreshTokenExpireDays;            // refreshToken 过期时间（天）

    private SecretKey key;                          // HMAC-SHA 密钥对象

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // 初始化密钥
    }

    public String generateAccessToken(Long userId, String phone, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))    // 主题：用户ID
                .claim("phone", phone)              // 自定义字段：手机号
                .claim("role", role)                // 自定义字段：角色
                .issuedAt(Date.from(now))           // 签发时间
                .expiration(Date.from(now.plus(accessTokenExpireHours, ChronoUnit.HOURS))) // 过期时间
                .signWith(key)                      // HMAC-SHA256 签名
                .compact();                         // 生成 JWT 字符串
    }

    public String generateRefreshToken(Long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))    // 主题：用户ID
                .claim("type", "refresh")           // 标记为 refresh token
                .issuedAt(Date.from(now))           // 签发时间
                .expiration(Date.from(now.plus(refreshTokenExpireDays, ChronoUnit.DAYS))) // 过期时间
                .signWith(key)                      // HMAC-SHA256 签名
                .compact();                         // 生成 JWT 字符串
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)                    // 用密钥验证签名
                .build()
                .parseSignedClaims(token)           // 解析 token
                .getPayload();                      // 获取载荷（claims）
    }

    public long getRefreshTokenExpireDays() {
        return refreshTokenExpireDays;
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);                      // 解析成功 = 有效
            return true;
        } catch (Exception e) {                     // 过期/签名错误等都算无效
            return false;
        }
    }
}