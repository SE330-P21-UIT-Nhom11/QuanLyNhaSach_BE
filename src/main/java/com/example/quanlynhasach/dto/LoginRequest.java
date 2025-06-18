package com.example.quanlynhasach.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request payload")
public class LoginRequest {
    @Schema(description = "User email address", example = "user@example.com", required = true)
    public String email;
    
    @Schema(description = "User password", example = "password123", required = true)
    public String password;

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}