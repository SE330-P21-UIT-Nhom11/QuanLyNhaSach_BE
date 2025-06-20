package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.dto.CreateUserByEmailRequest;

/**
 * Service interface for admin operations
 */
public interface AdminService {
    
    /**
     * Create a new user account by admin with only email
     * - Role is automatically set to "USER"
     * - Password is auto-generated
     * - Sends welcome email with credentials
     * 
     * @param createUserRequest Contains only email
     * @return Created user with encrypted password
     * @throws IllegalArgumentException if email is invalid or user already exists
     * @throws RuntimeException if user creation fails
     */
    User createUserAccount(CreateUserByEmailRequest createUserRequest);
}
