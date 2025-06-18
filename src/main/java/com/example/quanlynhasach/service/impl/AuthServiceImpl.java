package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.dto.LoginResponse;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.repository.UserRepository;
import com.example.quanlynhasach.service.AuthService;
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
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Object login(String email, String password) {
        // Validate input
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password must not be null or empty");
        }
        
        try {
            // Find user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("Invalid email or password");
            }
            
            // Check password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }            // Create and return login response
            LoginResponse response = new LoginResponse();
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setRole(user.getRole().toString()); // Convert enum to string
            response.setMessage("Login successful");
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean logout(String token) {
        try {
            // Implement logout logic here
            // For now, just validate the token and return success
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            

            return true;
            
        } catch (Exception e) {
            System.err.println("Logout error: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void forgotPassword(String email) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
        
        try {
            // Find user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("No account found with email: " + email);
            }
            
            // TODO: Generate reset token and send email
            // For now, just log the action
            System.out.println("Password reset requested for email: " + email);
            // In a real implementation, you would:
            // 1. Generate a secure reset token
            // 2. Store it with expiration time
            // 3. Send email with reset link
            
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
