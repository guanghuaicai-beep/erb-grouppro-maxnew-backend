package com.nick.myApp.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nick.myApp.models.Users;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // ==================== 登入 token 配置 ====================

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-days:120}")
    private long jwtExpirationDays;

    private SecretKey getJwtSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 生成登入 access token
    public String generateAccessToken(String username) {
        long expiryMs = jwtExpirationDays * 24 * 60 * 60 * 1000L;
        Date expiryDate = new Date(System.currentTimeMillis() + expiryMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getJwtSigningKey())
                .compact();
    }

    // 從登入 token 提取 username
    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, Claims::getSubject, getJwtSigningKey());
    }

    // 驗證登入 token
    public boolean validateAccessToken(String token, String username) {
        try {
            final String extractedUsername = extractUsernameFromAccessToken(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token, getJwtSigningKey()));
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== 重設密碼 token 配置 ====================

    @Value("${reset.secret}")
    private String resetSecret;

    @Value("${reset.expiration-minutes:30}")
    private long resetExpirationMinutes;

    private SecretKey getResetSigningKey() {
        return Keys.hmacShaKeyFor(resetSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 生成重設 token（取代你原本 ResetToken.generateResetToken）
    public String generateResetToken(Users user) {
        long expiryMs = resetExpirationMinutes * 60 * 1000L;
        Date expiryDate = new Date(System.currentTimeMillis() + expiryMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("type", "password_reset")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getResetSigningKey())
                .compact();
    }

    // 驗證重設 token 並返回 email（取代你原本 ResetToken.validateAndGetEmail）
    public String validateResetTokenAndGetEmail(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token is empty or null");
        }

        System.out.println("Validating reset token: " + token.substring(0, Math.min(50, token.length())) + "...");

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getResetSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("Parsed claims: " + claims);
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Type: " + claims.get("type", String.class));
            System.out.println("Expiration: " + claims.getExpiration());

            if (!"password_reset".equals(claims.get("type", String.class))) {
                System.out.println("Invalid token type");
                throw new IllegalArgumentException("無效的重設 token type");
            }

            if (claims.getExpiration().before(new Date())) {
                System.out.println("Token expired at: " + claims.getExpiration());
                throw new IllegalArgumentException("重設連結已過期");
            }

            return claims.getSubject();  // email

        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("Signature validation failed - likely wrong secret key");
            e.printStackTrace();
            throw new IllegalArgumentException("無效的 token signature");

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("Token expired at: " + e.getClaims().getExpiration());
            throw new IllegalArgumentException("重設連結已過期");

        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("Malformed token");
            e.printStackTrace();
            throw new IllegalArgumentException("無效的 token 格式");

        } catch (Exception e) {
            System.out.println("Unexpected JWT error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException("無效或過期嘅重設 token: " + e.getMessage());
        }
    }

    // ==================== 通用 helper 方法 ====================

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token, SecretKey key) {
        return extractExpiration(token, key).before(new Date());
    }

    private Date extractExpiration(String token, SecretKey key) {
        return extractClaim(token, Claims::getExpiration, key);
    }
}