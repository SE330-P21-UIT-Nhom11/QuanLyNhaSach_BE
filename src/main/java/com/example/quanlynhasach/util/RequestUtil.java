package com.example.quanlynhasach.util;

import com.example.quanlynhasach.model.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestUtil {

    /**
     * Lấy email của user hiện tại từ request
     */
    public static String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return (String) request.getAttribute("userEmail");
        }
        return null;
    }

    /**
     * Lấy role của user hiện tại từ request
     */
    public static Role getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return (Role) request.getAttribute("userRole");
        }
        return null;
    }

    /**
     * Kiểm tra user hiện tại có role cụ thể không
     */
    public static boolean hasRole(Role role) {
        Role currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equals(role);
    }

    /**
     * Kiểm tra user hiện tại có phải admin không
     */
    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Kiểm tra user hiện tại có phải employee không
     */
    public static boolean isEmployee() {
        Role currentRole = getCurrentUserRole();
        return currentRole != null && (currentRole.equals(Role.EMPLOYEE) || currentRole.equals(Role.ADMIN));
    }

    /**
     * Kiểm tra user hiện tại có phải user thông thường không
     */
    public static boolean isUser() {
        return hasRole(Role.USER);
    }

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
