package com.example.quanlynhasach.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Authentication failed: " + e.getMessage());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Invalid email or password");
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Access denied: " + e.getMessage());
        error.put("status", HttpStatus.FORBIDDEN.value());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpiredException(TokenExpiredException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Token expired: " + e.getMessage());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("code", "TOKEN_EXPIRED");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(InvalidTokenException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", "Invalid token: " + e.getMessage());
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("code", "INVALID_TOKEN");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
