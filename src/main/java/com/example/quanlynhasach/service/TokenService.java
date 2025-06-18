package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.dto.TokenResponse;

public interface TokenService {
    TokenResponse generateAuthTokens(User user);

    void revokeToken(String token, int userId);

    
}
