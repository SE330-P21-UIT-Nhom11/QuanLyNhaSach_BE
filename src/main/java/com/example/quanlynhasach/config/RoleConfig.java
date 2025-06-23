package com.example.quanlynhasach.config;

import com.example.quanlynhasach.model.enums.Role;
import com.example.quanlynhasach.model.enums.Permission;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class RoleConfig {
    
    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new HashMap<>();
    
    static {
        // USER Permissions - Khách hàng thông thường
        Set<Permission> userPermissions = Set.of(
            // Có thể xem sản phẩm
            Permission.PRODUCT_READ,
            Permission.CATEGORY_READ,
            Permission.AUTHOR_READ,
            Permission.PUBLISHER_READ,
            
            // Quản lý giỏ hàng của mình
            Permission.CART_READ,
            Permission.CART_WRITE,
            
            // Quản lý đơn hàng của mình
            Permission.ORDER_READ,
            Permission.ORDER_WRITE,
            
            // Quản lý thanh toán của mình
            Permission.PAYMENT_READ,
            Permission.PAYMENT_WRITE,
            
            // Viết và quản lý review của mình
            Permission.REVIEW_READ,
            Permission.REVIEW_WRITE,
            Permission.REVIEW_DELETE // Chỉ xóa review của mình
        );
        ROLE_PERMISSIONS.put(Role.USER, userPermissions);
        
        // EMPLOYEE Permissions - Nhân viên
        Set<Permission> employeePermissions = new HashSet<>(userPermissions);
        employeePermissions.addAll(Set.of(
            // Quản lý sản phẩm
            Permission.PRODUCT_WRITE,
            Permission.CATEGORY_WRITE,
            Permission.AUTHOR_WRITE,
            Permission.PUBLISHER_WRITE,
            
            // Xem tất cả đơn hàng
            Permission.ORDER_READ_ALL,
            Permission.ORDER_DELETE,
            
            // Xem tất cả thanh toán
            Permission.PAYMENT_READ_ALL,
            
            // Xóa review không phù hợp
            Permission.REVIEW_DELETE_ANY,
            
            // Xem thông tin user (để hỗ trợ)
            Permission.USER_READ
        ));
        ROLE_PERMISSIONS.put(Role.EMPLOYEE, employeePermissions);
        
        // ADMIN Permissions - Quản trị viên
        Set<Permission> adminPermissions = new HashSet<>(employeePermissions);
        adminPermissions.addAll(Set.of(
            // Quản lý sản phẩm hoàn toàn
            Permission.PRODUCT_DELETE,
            Permission.CATEGORY_DELETE,
            Permission.AUTHOR_DELETE,
            Permission.PUBLISHER_DELETE,
            
            // Quản lý user
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.USER_DELETE,
            
            // Quyền admin
            Permission.ADMIN_READ,
            Permission.ADMIN_WRITE,
            Permission.ADMIN_DELETE
        ));
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
}
