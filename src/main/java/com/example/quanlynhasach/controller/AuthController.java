package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.LoginRequest;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.service.UserService;
import com.example.quanlynhasach.service.SecurityAuditService;
import com.example.quanlynhasach.util.JwtUtil;
import com.example.quanlynhasach.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(name = "Authentication", description = "API for user authentication and authorization")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityAuditService securityAuditService;    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Login failed due to server error", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "Login credentials") @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            // Xác thực user
            User user = userService.getUserByEmail(loginRequest.getEmail()).orElse(null);
            if (user == null) {
                securityAuditService.logFailedLogin(loginRequest.getEmail(), "User not found", request);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(errorResponse);
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                securityAuditService.logFailedLogin(loginRequest.getEmail(), "Invalid password", request);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(errorResponse);
            }
            // Tạo tokens
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            // Set refresh token vào cookie
            CookieUtil.createRefreshTokenCookie(response, refreshToken);

            // Log successful login
            securityAuditService.logSuccessfulLogin(user.getEmail(), request);

            // Trả về access token trong response body
            Map<String, Object> loginResponse = new HashMap<>();
            loginResponse.put("success", true);
            loginResponse.put("message", "Login successful");
            loginResponse.put("accessToken", accessToken);
            loginResponse.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()
            ));

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            securityAuditService.logFailedLogin(loginRequest.getEmail(), "System error: " + e.getMessage(), request);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }    }    
    
    
    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logout user and clear refresh token cookie")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Logout failed due to server error", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> logout(HttpServletResponse response) {
        // Xóa refresh token cookie
        CookieUtil.deleteRefreshTokenCookie(response);

        Map<String, Object> logoutResponse = new HashMap<>();
        logoutResponse.put("success", true);
        logoutResponse.put("message", "Logout successful");

        return ResponseEntity.ok(logoutResponse);
    }    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get logged-in user information")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Failed to get user info due to server error", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        try {
            String userEmail = (String) request.getAttribute("userEmail");

            if (userEmail == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Not authenticated");
                return ResponseEntity.status(401).body(errorResponse);
            }
            User user = userService.getUserByEmail(userEmail).orElse(null);
            if (user == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "User not found");
                return ResponseEntity.status(404).body(errorResponse);
            }

            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("success", true);
            userResponse.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()
            ));

            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Failed to get user info: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Register - Đăng ký user mới
     */
    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Registration successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "409", description = "User with this email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "500", description = "Registration failed due to server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            })
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody User registerRequest,
            HttpServletResponse response) {

        try {
            // Validate input
            if (registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Email and password are required");
                return ResponseEntity.status(400).body(errorResponse);
            }

            // Check if user already exists
            if (userService.getUserByEmail(registerRequest.getEmail()).isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "User with this email already exists");
                return ResponseEntity.status(409).body(errorResponse);
            }

            // Hash password
            registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

            // Set default role if not specified
            if (registerRequest.getRole() == null) {
                registerRequest.setRole(com.example.quanlynhasach.model.enums.Role.USER);
            }

            // Create user
            User createdUser = userService.createUser(registerRequest);

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(createdUser);
            String refreshToken = jwtUtil.generateRefreshToken(createdUser);

            // Set refresh token in cookie
            CookieUtil.createRefreshTokenCookie(response, refreshToken);

            Map<String, Object> registerResponse = new HashMap<>();
            registerResponse.put("success", true);
            registerResponse.put("message", "Registration successful");
            registerResponse.put("accessToken", accessToken);
            registerResponse.put("user", Map.of(
                    "id", createdUser.getId(),
                    "email", createdUser.getEmail(),
                    "name", createdUser.getName(),
                    "role", createdUser.getRole()
            ));

            return ResponseEntity.status(201).body(registerResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
