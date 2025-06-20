package com.example.quanlynhasach.aspect;

import com.example.quanlynhasach.annotation.RequirePermission;
import com.example.quanlynhasach.annotation.RequireRole;
import com.example.quanlynhasach.config.RoleConfig;
import com.example.quanlynhasach.model.enums.Permission;
import com.example.quanlynhasach.model.enums.Role;
import com.example.quanlynhasach.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private UserService userService;

    @Before("@annotation(requireRole)")
    public void checkRole(JoinPoint joinPoint, RequireRole requireRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String email = authentication.getName();
        Role userRole = userService.getUserRole(email);
        
        Role[] requiredRoles = requireRole.value();
        boolean requireAll = requireRole.requireAll();
        
        boolean hasAccess;
        if (requireAll) {
            // User cần có tất cả roles
            hasAccess = Arrays.asList(requiredRoles).contains(userRole);
        } else {
            // User chỉ cần có ít nhất một role
            hasAccess = Arrays.asList(requiredRoles).contains(userRole);
        }
        
        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Access denied. Required role(s): " + Arrays.toString(requiredRoles));
        }
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String email = authentication.getName();
        Role userRole = userService.getUserRole(email);
        
        Permission[] requiredPermissions = requirePermission.value();
        boolean requireAll = requirePermission.requireAll();
        
        Set<Permission> userPermissions = RoleConfig.getPermissions(userRole);
        
        boolean hasAccess;
        if (requireAll) {
            // User cần có tất cả permissions
            hasAccess = userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            // User chỉ cần có ít nhất một permission
            hasAccess = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }
        
        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Access denied. Required permission(s): " + Arrays.toString(requiredPermissions));
        }
    }
}
