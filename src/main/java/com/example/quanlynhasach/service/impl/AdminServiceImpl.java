package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.dto.CreateUserByEmailRequest;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.model.enums.Role;
import com.example.quanlynhasach.repository.UserRepository;
import com.example.quanlynhasach.service.AdminService;
import com.example.quanlynhasach.service.EmailService;
import com.example.quanlynhasach.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AdminService for admin operations
 */
@Slf4j
@Service
@Transactional
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User createUserAccount(CreateUserByEmailRequest createUserRequest) {
        try {
            log.info("Creating new user account for email: {}", createUserRequest.getEmail());
            
            // Validate email format first
            if (!emailService.isValidEmail(createUserRequest.getEmail())) {
                throw new IllegalArgumentException("Invalid email address: " + createUserRequest.getEmail());
            }
            
            // Check if user already exists
            if (userRepository.findByEmail(createUserRequest.getEmail()) != null) {
                throw new IllegalArgumentException("User with email " + createUserRequest.getEmail() + " already exists");
            }
            
            // Generate temporary password
            String temporaryPassword = PasswordUtil.generateTemporaryPassword();
            
            // Extract name from email (before @)
            String userName = createUserRequest.getEmail().substring(0, createUserRequest.getEmail().indexOf("@"));
            
            // Create new user with auto-generated values
            User newUser = new User();
            newUser.setName(userName); // Use email prefix as name
            newUser.setEmail(createUserRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(temporaryPassword));
            newUser.setRole(Role.USER); // Always set role to USER
            newUser.setPhone(""); // Default empty
            newUser.setAddress(""); // Default empty
            
            // Save user to database
            User savedUser = userRepository.save(newUser);
            
            // Send welcome email with account credentials
            try {
                emailService.sendWelcomeEmail(
                    savedUser.getEmail(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    temporaryPassword
                );
                log.info("Welcome email sent successfully to: {}", savedUser.getEmail());
            } catch (Exception emailException) {
                log.error("Failed to send welcome email to: {}, but user was created successfully", 
                         savedUser.getEmail(), emailException);
                // Don't fail the entire operation if email fails
            }
            
            log.info("User account created successfully with ID: {}", savedUser.getId());
            return savedUser;
            
        } catch (Exception e) {
            log.error("Failed to create user account for email: {}", createUserRequest.getEmail(), e);
            throw new RuntimeException("Failed to create user account: " + e.getMessage(), e);
        }
    }
}
