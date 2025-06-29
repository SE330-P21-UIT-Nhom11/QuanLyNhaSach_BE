package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.CreateUserByEmailRequest;
import com.example.quanlynhasach.dto.LoginRequest;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.service.AdminService;
import com.example.quanlynhasach.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(name = "users", description = "User management operations")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AdminService adminService;

    /**
     * Tạo tài khoản mới cho người dùng (Public endpoint)
     * Người dùng chỉ cần cung cấp email để tạo tài khoản
     * Kiểm tra email hợp lệ và chưa tồn tại trong hệ thống
     */
    @PostMapping("/register")
    @Operation(summary = "User Self Registration", 
               description = "Allows users to create their own account by providing email. Email must be valid and not already exist in the system. Role=USER and password auto-generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User account created successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid email format, empty email, or email already exists", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Account creation failed due to server error", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> registerUser(
            @Parameter(description = "User email address", example = "user@example.com") 
            @Valid @RequestBody CreateUserByEmailRequest createUserRequest) {
        
        try {
            String email = createUserRequest.getEmail();
            log.info("User self-registration attempt for email: {}", email);
            
            // Kiểm tra email có hợp lệ không
            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "INVALID_EMAIL");
                errorResponse.put("message", "Email không được để trống");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Kiểm tra định dạng email
            if (!isValidEmail(email)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "INVALID_EMAIL_FORMAT");
                errorResponse.put("message", "Định dạng email không hợp lệ");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Kiểm tra email đã tồn tại hay chưa
            Optional<User> existingUser = userService.getUserByEmail(email);
            if (existingUser.isPresent()) {
                log.warn("Registration failed: Email already exists: {}", email);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "EMAIL_ALREADY_EXISTS");
                errorResponse.put("message", "Email này đã được sử dụng. Vui lòng sử dụng email khác hoặc đăng nhập.");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Tạo tài khoản mới
            User createdUser = adminService.createUserAccount(createUserRequest);
            log.info("User account created successfully for email: {}", email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tài khoản đã được tạo thành công");
            response.put("user", Map.of(
                    "id", createdUser.getId(),
                    "name", createdUser.getName(),
                    "email", createdUser.getEmail(),
                    "role", createdUser.getRole().toString()
            ));
            response.put("note", "Email chào mừng với mật khẩu tạm thời đã được gửi tới: " + createdUser.getEmail());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for user registration: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "INVALID_INPUT");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
            
        } catch (Exception e) {
            log.error("Failed to register user account", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "REGISTRATION_FAILED");
            errorResponse.put("message", "Không thể tạo tài khoản: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Kiểm tra định dạng email có hợp lệ không
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Regex pattern cho email validation
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailPattern);
    }

    // Lấy tất cả user
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy user theo id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            Optional<User> user = userService.getUserById(id);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        try {
            Optional<User> user = userService.getUserByEmail(email);
            return user.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Tạo user mới
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cập nhật user
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa người dùng có ID = " + id);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest login) {
        boolean success = userService.login(login.getEmail(), login.getPassword());
        if (success) {
            return ResponseEntity.ok("Login successful!");
        } else {
            return ResponseEntity.badRequest().body("Invalid email or password.");
        }
    }
}