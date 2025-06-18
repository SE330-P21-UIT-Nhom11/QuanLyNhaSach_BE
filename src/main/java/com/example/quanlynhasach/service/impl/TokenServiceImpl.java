package com.example.quanlynhasach.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.quanlynhasach.dto.TokenResponse;
import com.example.quanlynhasach.model.Token;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.repository.TokenRepository;
import com.example.quanlynhasach.service.TokenService;
import org.springframework.stereotype.Service;
import com.example.quanlynhasach.util.JwtUtil;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public TokenResponse generateAuthTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        Token token = new Token();
        token.setUserId(user.getId());
        token.setTokenValue(refreshToken);
        token.setExpirationTime(java.time.LocalDateTime.now().plusDays(30));
        tokenRepository.save(token);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public void revokeToken(String token, int userId) {
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        if (existingToken != null && existingToken.getUserId() == userId) {
            existingToken.setRevoked(true);
            tokenRepository.save(existingToken);
        }
    }
}
