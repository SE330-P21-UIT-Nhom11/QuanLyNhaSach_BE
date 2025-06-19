package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.LoginRequest;
import com.example.quanlynhasach.dto.TokenResponse;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.service.EmailService;
import com.example.quanlynhasach.service.SecurityAuditService;
import com.example.quanlynhasach.service.TokenService;
import com.example.quanlynhasach.service.UserService;
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
@Tag(name = "authentication", description = "API for user authentication and authorization")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityAuditService securityAuditService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and return access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Login failed due to server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
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
                return ResponseEntity.status(401).body(errorResponse);            } // Tạo tokens bằng TokenService
            TokenResponse tokenResponse = tokenService.generateAuthTokens(user);

            // Set refresh token vào cookie
            CookieUtil.createRefreshTokenCookie(response, tokenResponse.getRefreshToken());

            // Log successful login
            securityAuditService.logSuccessfulLogin(user.getEmail(), request);            // Trả về access token trong response body
            Map<String, Object> loginResponse = new HashMap<>();
            loginResponse.put("success", true);
            loginResponse.put("message", "Login successful");
            loginResponse.put("accessToken", tokenResponse.getAccessToken());
            loginResponse.put("user", Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole()));

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            securityAuditService.logFailedLogin(loginRequest.getEmail(), "System error: " + e.getMessage(), request);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Logout user and clear refresh token cookie")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Logout failed due to server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy refresh token từ cookie
            String refreshToken = CookieUtil.getRefreshTokenFromCookie(request);
            
            // Nếu có refresh token, revoke nó trong database
            if (refreshToken != null) {
                tokenService.revokeToken(refreshToken);
            }
            
            // Xóa refresh token cookie
            CookieUtil.deleteRefreshTokenCookie(response);

            Map<String, Object> logoutResponse = new HashMap<>();
            logoutResponse.put("success", true);
            logoutResponse.put("message", "Logout successful");

            return ResponseEntity.ok(logoutResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get logged-in user information")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Failed to get user info due to server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
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
                    "role", user.getRole()));

            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Failed to get user info: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // @PostMapping("/forgot-password")
    // @Operation(summary = "Forgot Password", description = "Send password reset link to user's email")
    // @ApiResponses(value = {
    //         @ApiResponse(responseCode = "200", description = "Password reset link sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    //         @ApiResponse(responseCode = "400", description = "Invalid email format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    //         @ApiResponse(responseCode = "404", description = "User not found with the provided email", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
    //         @ApiResponse(responseCode = "500", description = "Failed to send password reset link due to server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    // })
    // public ResponseEntity<Map<String, Object>> forgotPassword(
    //         @Parameter(description = "User email for password reset") @RequestParam String email) {
    //     try {
    //         if (email == null || email.trim().isEmpty()) {
    //             Map<String, Object> errorResponse = new HashMap<>();
    //             errorResponse.put("error", true);
    //             errorResponse.put("message", "Email must not be null or empty");
    //             return ResponseEntity.badRequest().body(errorResponse);
    //         }

    //         // Validate email format
    //         if (!emailService.isValidEmail(email)) {
    //             Map<String, Object> errorResponse = new HashMap<>();
    //             errorResponse.put("error", true);
    //             errorResponse.put("message", "Invalid email format");
    //             return ResponseEntity.badRequest().body(errorResponse);
    //         }

    //         // Check if the user exists
    //         User user = userService.getUserByEmail(email).orElse(null);
    //         if (user == null) {
    //             Map<String, Object> errorResponse = new HashMap<>();
    //             errorResponse.put("error", true);
    //             errorResponse.put("message", "User not found");
    //             return ResponseEntity.status(404).body(errorResponse);
    //         }

    //         // Generate reset token
    //         String resetToken = jwtUtil.generateResetToken(user);

    //         // Create password reset link
    //         String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

    //         // Send password reset email
    //         emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);

    //         // Log security event
    //         securityAuditService.logPasswordResetRequest(user.getEmail(),
    //                 HttpServletRequest.class.cast(
    //                         org.springframework.web.context.request.RequestContextHolder
    //                                 .currentRequestAttributes()
    //                                 .resolveReference(
    //                                         org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST)));

    //         Map<String, Object> successResponse = new HashMap<>();
    //         successResponse.put("success", true);
    //         successResponse.put("message", "Password reset link sent successfully");
    //         return ResponseEntity.ok(successResponse);

    //     } catch (Exception e) {
    //         Map<String, Object> errorResponse = new HashMap<>();
    //         errorResponse.put("error", true);
    //         errorResponse.put("message", "Failed to send password reset link: " + e.getMessage());
    //         return ResponseEntity.status(500).body(errorResponse);
    //     }
    // }

    /*
     * EMAIL SERVICE USAGE EXAMPLES:
     * 
     * 1. To send welcome email with account credentials when creating a new user:
     * 
     * // Generate temporary password
     * String temporaryPassword = PasswordUtil.generateTemporaryPassword();
     * 
     * // Encode password for storage
     * String encodedPassword = passwordEncoder.encode(temporaryPassword);
     * user.setPassword(encodedPassword);
     * 
     * // Save user to database
     * User savedUser = userService.createUser(user);
     * 
     * // Send welcome email with credentials
     * emailService.sendWelcomeEmail(
     * savedUser.getEmail(), // to
     * savedUser.getName(), // userName
     * savedUser.getEmail(), // userEmail (login username)
     * temporaryPassword // temporary password (plain text)
     * );
     * 
     * 2. To validate email before using it:
     * 
     * if (!emailService.isValidEmail(email)) {
     * throw new IllegalArgumentException("Invalid email address");
     * }
     * 
     * 3. The sendWelcomeEmail method will automatically:
     * - Validate email format and deliverability
     * - Send a beautifully formatted HTML email with:
     * * Account credentials (email/username and temporary password)
     * * Security instructions to change password
     * * Professional styling with QuanLyNhaSach branding
     * - Log success/failure for monitoring
     */
}
