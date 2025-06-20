package com.example.quanlynhasach.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for creating user account by admin with only email
 * Role will automatically be set to "USER"
 * Password will be auto-generated
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserByEmailRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}
