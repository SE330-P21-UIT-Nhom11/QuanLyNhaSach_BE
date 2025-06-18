package com.example.quanlynhasach.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.example.quanlynhasach.exception.InvalidTokenException;

import java.util.Date;
import java.security.Key;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.example.quanlynhasach.model.User;

import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    
    @Value("${JWT_SECRET}")
    private String jwtSecret;
    
    @Value("${JWT_ACCESS_EXPIRATION:900000}")
    private long accessTokenExpirationTime;
    
    @Value("${JWT_REFRESH_EXPIRATION:2592000000}")
    private long refreshTokenExpirationTime;
    
    @Value("${JWT_RESET_EXPIRATION:300000}")
    private long resetTokenExpirationTime;

    private Key secretKey;

    @PostConstruct
    private void initializeValues() {
        if (jwtSecret != null && jwtSecret.length() >= 32) {
            this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        } else {
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String generateResetToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + resetTokenExpirationTime))
                .signWith(secretKey)
                .compact();
    }    public String extractEmail(String token) {
        return parseToken(token).getSubject();
    }

    public String extractRole(String token) {
        return parseToken(token).get("role", String.class);
    }    public boolean isTokenExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed");
        }
    }

    public boolean validateToken(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed");
        }
    }

    public boolean verifyToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid JWT token");
        }
    }

    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
