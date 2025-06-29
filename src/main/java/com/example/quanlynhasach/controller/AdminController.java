package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Controller - Simplified for core admin functions only
 * User registration has been moved to UserController as a public endpoint
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(name = "admin", description = "Core admin operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Admin-specific endpoints can be added here
    // User registration has been moved to /api/users/register as public endpoint
}
