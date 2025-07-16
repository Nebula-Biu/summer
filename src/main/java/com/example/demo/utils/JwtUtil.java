package com.example.demo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.security.Key;

/**
 * JWT工具类
 * 提供生成和校验JWT Token的方法
 */
public class JwtUtil {
    private static final String SECRET = "mySecretKeyForJwtDemoProject1234567890"; // 建议放到配置文件
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24小时
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 生成token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析token
    public static String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 校验token
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
} 