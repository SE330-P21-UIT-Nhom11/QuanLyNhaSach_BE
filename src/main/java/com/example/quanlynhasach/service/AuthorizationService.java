package com.example.quanlynhasach.service;

import com.example.quanlynhasach.config.RoleConfig;
import com.example.quanlynhasach.model.enums.Permission;
import com.example.quanlynhasach.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthorizationService {

    @Autowired
    private UserService userService;

    /**
     * Kiểm tra user hiện tại có quyền cụ thể không
     */
    public boolean hasPermission(Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        Role userRole = userService.getUserRole(email);
        return RoleConfig.hasPermission(userRole, permission);
    }

    /**
     * Kiểm tra user hiện tại có một trong những quyền được chỉ định không
     */
    public boolean hasAnyPermission(Permission... permissions) {
        for (Permission permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra user hiện tại có tất cả quyền được chỉ định không
     */
    public boolean hasAllPermissions(Permission... permissions) {
        for (Permission permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Kiểm tra user hiện tại có role cụ thể không
     */
    public boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        Role userRole = userService.getUserRole(email);
        return userRole == role;
    }

    /**
     * Kiểm tra user hiện tại có ít nhất một trong các role được chỉ định không
     */
    public boolean hasAnyRole(Role... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        Role userRole = userService.getUserRole(email);
        
        for (Role role : roles) {
            if (userRole == role) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lấy role của user hiện tại
     */
    public Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        return userService.getUserRole(email);
    }

    /**
     * Lấy tất cả quyền của user hiện tại
     */
    public Set<Permission> getCurrentUserPermissions() {
        Role role = getCurrentUserRole();
        return role != null ? RoleConfig.getPermissions(role) : Set.of();
    }

    /**
     * Kiểm tra user có thể thực hiện action trên resource không
     */
    public boolean canPerformAction(String resource, String action) {
        Role role = getCurrentUserRole();
        return role != null && RoleConfig.canPerformAction(role, resource, action);
    }
}
