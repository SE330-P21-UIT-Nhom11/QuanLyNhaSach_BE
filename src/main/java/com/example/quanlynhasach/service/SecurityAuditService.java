package com.example.quanlynhasach.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SecurityAuditService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log successful login
     */
    public void logSuccessfulLogin(String email, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Successful login - User: %s, IP: %s, UserAgent: %s, Time: %s",
            email,
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            LocalDateTime.now().format(formatter)
        );
        logger.info(message);
    }

    /**
     * Log failed login attempt
     */
    public void logFailedLogin(String email, String reason, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Failed login attempt - User: %s, Reason: %s, IP: %s, UserAgent: %s, Time: %s",
            email,
            reason,
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            LocalDateTime.now().format(formatter)
        );
        logger.warn(message);
    }

    /**
     * Log successful logout
     */
    public void logLogout(String email, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Logout - User: %s, IP: %s, Time: %s",
            email,
            getClientIpAddress(request),
            LocalDateTime.now().format(formatter)
        );
        logger.info(message);
    }

    /**
     * Log token refresh
     */
    public void logTokenRefresh(String email, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Token refresh - User: %s, IP: %s, Time: %s",
            email,
            getClientIpAddress(request),
            LocalDateTime.now().format(formatter)
        );
        logger.info(message);
    }

    /**
     * Log suspicious activity
     */
    public void logSuspiciousActivity(String activity, String email, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Suspicious activity - Activity: %s, User: %s, IP: %s, UserAgent: %s, Time: %s",
            activity,
            email != null ? email : "Unknown",
            getClientIpAddress(request),
            request.getHeader("User-Agent"),
            LocalDateTime.now().format(formatter)
        );
        logger.warn(message);
    }

    /**
     * Log access denied events
     */
    public void logAccessDenied(String email, String resource, String action, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Access denied - User: %s, Resource: %s, Action: %s, IP: %s, Time: %s",
            email,
            resource,
            action,
            getClientIpAddress(request),
            LocalDateTime.now().format(formatter)
        );
        logger.warn(message);
    }

    /**
     * Log password change
     */
    public void logPasswordChange(String email, HttpServletRequest request) {
        String message = String.format(
            "[SECURITY] Password changed - User: %s, IP: %s, Time: %s",
            email,
            getClientIpAddress(request),
            LocalDateTime.now().format(formatter)
        );
        logger.info(message);
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
