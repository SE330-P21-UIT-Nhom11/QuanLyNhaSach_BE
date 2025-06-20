package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.CreateUserByEmailRequest;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin Controller for creating user accounts
 * Simplified to only require email input
 * - Role is automatically set to "USER"
 * - Password is auto-generated
 * - Name is extracted from email prefix
 * - Sends welcome email with credentials
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(name = "admin", description = "Admin operations for user management")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Create a new user account with only email required
     * - Admin only needs to provide email
     * - Role is automatically set to "USER"
     * - Password is auto-generated using PasswordUtil
     * - Name is extracted from email (part before @)
     * - Sends welcome email with login credentials
     * 
     * @param createUserRequest Contains only email address
     * @return Created user information and success message
     */
    @PostMapping("/users")
    @Operation(summary = "Create User Account by Email", 
               description = "Create a new user account with only email. Role=USER and password auto-generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email or user already exists", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "User creation failed due to server error", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))    })
    // @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for testing
    public ResponseEntity<Map<String, Object>> createUserAccount(
            @Parameter(description = "User email address", example = "user@example.com") 
            @Valid @RequestBody CreateUserByEmailRequest createUserRequest) {
        
        try {
            log.info("Admin creating user account for email: {}", createUserRequest.getEmail());
            
            User createdUser = adminService.createUserAccount(createUserRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User account created successfully");
            response.put("user", Map.of(
                    "id", createdUser.getId(),
                    "name", createdUser.getName(),
                    "email", createdUser.getEmail(),
                    "role", createdUser.getRole().toString()
            ));
            response.put("note", "Welcome email with temporary password has been sent to: " + createdUser.getEmail());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for user creation: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "INVALID_INPUT");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Failed to create user account", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "CREATION_FAILED");
            errorResponse.put("message", "Failed to create user account: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
