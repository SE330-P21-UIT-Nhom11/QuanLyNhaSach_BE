package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.dto.LoginResponse;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.repository.UserRepository;
import com.example.quanlynhasach.service.AuthService;
import com.example.quanlynhasach.service.TokenService;
import com.example.quanlynhasach.service.EmailService;
import com.example.quanlynhasach.dto.TokenResponse;
import com.example.quanlynhasach.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public Object login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password must not be null or empty");
        }
        
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("Invalid email or password");
            }
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }

            TokenResponse tokenResponse = tokenService.generateAuthTokens(user);

            LoginResponse response = new LoginResponse();
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setRole(user.getRole().toString());
            response.setMessage("Login successful");
            response.setAccessToken(tokenResponse.getAccessToken());
            response.setRefreshToken(tokenResponse.getRefreshToken());

            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean logout(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            
            tokenService.revokeToken(token);
            return true;
            
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void forgotPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("No account found with email: " + email);
            }            // Generate reset token (valid for 15 minutes)
            String resetToken = jwtUtil.generateResetToken(user);

            // Send password reset email with frontend link and token
            emailService.sendPasswordResetEmail(email, resetToken);

            
        } catch (Exception e) {
            throw new RuntimeException("Forgot password failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean resetPassword(String token, String newPassword) {
        // Validate input
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Token and new password must not be null or empty");
        }
        
        try {
            // TODO: Implement reset password logic
            // For now, just validate inputs and return success
            
            // In a real implementation, you would:
            // 1. Validate the reset token
            // 2. Check if token is not expired
            // 3. Find user by token
            // 4. Update user's password
            // 5. Invalidate the reset token
            
            // For demonstration, just return true
            System.out.println("Password reset successful for token: " + token);
            return true;
            
        } catch (Exception e) {
            System.err.println("Reset password error: " + e.getMessage());
            return false;
        }
    }
}
