package com.example.quanlynhasach.config;

import com.example.quanlynhasach.model.enums.Role;
import com.example.quanlynhasach.model.enums.Permission;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class RoleConfig {
    
    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        Set<Permission> userPermissions = Set.of(
            Permission.PRODUCT_READ,
            Permission.CATEGORY_READ,
            Permission.AUTHOR_READ,
            Permission.PUBLISHER_READ,
            
            Permission.CART_READ,
            Permission.CART_WRITE,
            
            Permission.ORDER_READ,
            Permission.ORDER_WRITE,
        
            Permission.PAYMENT_READ,
            Permission.PAYMENT_WRITE,

            Permission.REVIEW_READ,
            Permission.REVIEW_WRITE,
            Permission.REVIEW_DELETE 
        );
        ROLE_PERMISSIONS.put(Role.USER, userPermissions);
        
        Set<Permission> employeePermissions = EnumSet.noneOf(Permission.class);
        for (Permission permission : Permission.values()) {
            if (!permission.getPermission().startsWith("admin:")) {
                employeePermissions.add(permission);
            }
        }
        ROLE_PERMISSIONS.put(Role.EMPLOYEE, employeePermissions);

        Set<Permission> adminPermissions = EnumSet.allOf(Permission.class);
        ROLE_PERMISSIONS.put(Role.ADMIN, adminPermissions);
    }
    
    public static Set<Permission> getPermissions(Role role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }
    
    public static boolean hasPermission(Role role, Permission permission) {
        return getPermissions(role).contains(permission);
    }
    
    public static Map<Role, Set<Permission>> getAllRolePermissions() {
        return Collections.unmodifiableMap(ROLE_PERMISSIONS);
    }
    
    /**
     * Kiểm tra xem role có quyền thực hiện action trên resource không
     */
    public static boolean canPerformAction(Role role, String resource, String action) {
        String permissionString = resource + ":" + action;
        return getPermissions(role).stream()
                .anyMatch(permission -> permission.getPermission().equals(permissionString));
    }
    
    /**
     * Kiểm tra xem role có phải là admin không
     */
    public static boolean isAdmin(Role role) {
        return role == Role.ADMIN;
    }
    
    /**
     * Kiểm tra xem role có phải là employee hoặc admin không
     */
    public static boolean isEmployeeOrAbove(Role role) {
        return role == Role.EMPLOYEE || role == Role.ADMIN;
    }
    
    /**
     * Lấy tất cả quyền admin
     */
    public static Set<Permission> getAdminPermissions() {
        return Set.of(Permission.ADMIN_READ, Permission.ADMIN_WRITE, Permission.ADMIN_DELETE);
    }
}
