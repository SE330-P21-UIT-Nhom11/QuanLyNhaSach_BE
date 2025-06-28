package com.example.quanlynhasach.middleware;

import com.example.quanlynhasach.config.RoleConfig;
import com.example.quanlynhasach.model.Token;
import com.example.quanlynhasach.model.enums.Permission;
import com.example.quanlynhasach.model.enums.Role;
import com.example.quanlynhasach.repository.TokenRepository;
import com.example.quanlynhasach.util.JwtUtil;
import com.example.quanlynhasach.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthorizationMiddleware implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    // Map các endpoint với quyền cần thiết
    private static final Map<String, Permission> ENDPOINT_PERMISSIONS = new HashMap<>();
    
    static {
        // Product endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/products", Permission.PRODUCT_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/products", Permission.PRODUCT_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/products", Permission.PRODUCT_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/products", Permission.PRODUCT_DELETE);
        
        // Category endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/categories", Permission.CATEGORY_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/categories", Permission.CATEGORY_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/categories", Permission.CATEGORY_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/categories", Permission.CATEGORY_DELETE);
        
        // Author endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/authors", Permission.AUTHOR_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/authors", Permission.AUTHOR_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/authors", Permission.AUTHOR_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/authors", Permission.AUTHOR_DELETE);
        
        // Publisher endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/publishers", Permission.PUBLISHER_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/publishers", Permission.PUBLISHER_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/publishers", Permission.PUBLISHER_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/publishers", Permission.PUBLISHER_DELETE);
        
        // Cart endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/cart", Permission.CART_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/cart", Permission.CART_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/cart", Permission.CART_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/cart", Permission.CART_WRITE);
        
        // Order endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/orders", Permission.ORDER_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/orders", Permission.ORDER_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/orders", Permission.ORDER_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/orders", Permission.ORDER_DELETE);
        ENDPOINT_PERMISSIONS.put("GET:/api/orders/all", Permission.ORDER_READ_ALL);
        
        // Payment endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/payments", Permission.PAYMENT_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/payments", Permission.PAYMENT_WRITE);
        ENDPOINT_PERMISSIONS.put("GET:/api/payments/all", Permission.PAYMENT_READ_ALL);
        
        // Review endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/reviews", Permission.REVIEW_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/reviews", Permission.REVIEW_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/reviews", Permission.REVIEW_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/reviews", Permission.REVIEW_DELETE);
        
        // User endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/users", Permission.USER_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/users", Permission.USER_WRITE);
        ENDPOINT_PERMISSIONS.put("PUT:/api/users", Permission.USER_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/users", Permission.USER_DELETE);
        
        // Admin endpoints
        ENDPOINT_PERMISSIONS.put("GET:/api/admin", Permission.ADMIN_READ);
        ENDPOINT_PERMISSIONS.put("POST:/api/admin", Permission.ADMIN_WRITE);
        ENDPOINT_PERMISSIONS.put("DELETE:/api/admin", Permission.ADMIN_DELETE);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();
        
        // Bỏ qua các endpoint public
        if (isPublicEndpoint(method, path)) {
            System.out.println("✓ Public endpoint accessed: " + method + " " + path);
            chain.doFilter(request, response);
            return;
        }
        
        System.out.println("✗ Protected endpoint accessed: " + method + " " + path);        // Lấy access token từ Bearer header
        String authHeader = httpRequest.getHeader("Authorization");
        String accessToken = null;
        boolean tokenRefreshed = false;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        
        // Nếu không có access token hoặc access token hết hạn, thử dùng refresh token
        boolean accessTokenExpired = false;
        if (accessToken != null) {
            try {
                accessTokenExpired = jwtUtil.isTokenExpired(accessToken);
            } catch (Exception e) {
                accessTokenExpired = true; // Treat invalid token as expired
            }
        }
          if (accessToken == null || accessTokenExpired) {
            String refreshToken = CookieUtil.getRefreshTokenFromCookie(httpRequest);
            
            if (refreshToken != null && !refreshToken.isEmpty()) {
                try {
                    // Verify token trước khi kiểm tra hết hạn
                    if (jwtUtil.verifyToken(refreshToken) && !jwtUtil.isTokenExpired(refreshToken)) {
                        // Kiểm tra xem refresh token có bị revoked hay không
                        Optional<Token> tokenInDb = tokenRepository.findByTokenValue(refreshToken);
                        if (tokenInDb.isPresent() && tokenInDb.get().isRevoked()) {
                            sendErrorResponse(httpResponse, 401, "Refresh token has been revoked");
                            return;
                        }
                        
                        // Lấy thông tin từ refresh token để tạo access token mới
                        String email = jwtUtil.extractEmail(refreshToken);
                        String role = jwtUtil.extractRole(refreshToken);
                        
                        // Generate access token mới
                        accessToken = jwtUtil.generateAccessToken(email, role);
                        tokenRefreshed = true;
                    } else {
                        sendErrorResponse(httpResponse, 401, "Refresh token expired");
                        return;
                    }
                } catch (Exception e) {
                    // Log lỗi để debug
                    System.out.println("Error processing refresh token: " + e.getMessage());
                    sendErrorResponse(httpResponse, 401, "Invalid refresh token");
                    return;
                }
            } else {
                // Nếu đây là endpoint public, cho phép truy cập
                if (isPublicEndpoint(method, path)) {
                    chain.doFilter(request, response);
                    return;
                }
                sendErrorResponse(httpResponse, 401, "Missing or invalid authorization token");
                return;
            }
        }
        try {
            // Lấy email từ token
            String email = jwtUtil.extractEmail(accessToken);
            
            // Lấy role từ token
            Role userRole = getUserRoleFromToken(accessToken);
            
            // Kiểm tra quyền
            String endpointKey = method + ":" + path;
            Permission requiredPermission = ENDPOINT_PERMISSIONS.get(endpointKey);
            
            if (requiredPermission != null && !RoleConfig.hasPermission(userRole, requiredPermission)) {
                sendErrorResponse(httpResponse, 403, "Insufficient permissions for this action");
                return;
            }
            // Thêm thông tin user vào request để controller sử dụng
            httpRequest.setAttribute("userEmail", email);
            httpRequest.setAttribute("userRole", userRole);
            httpRequest.setAttribute("accessToken", accessToken);
        } catch (Exception e) {
            // Log lỗi để debug
            System.out.println("Error processing token: " + e.getMessage());
            e.printStackTrace();
            
            // Nếu đây là endpoint public, vẫn cho phép truy cập
            if (isPublicEndpoint(method, path)) {
                chain.doFilter(request, response);
                return;
            }
            
            sendErrorResponse(httpResponse, 401, "Invalid token");
            return;
        }

        // Thực hiện request
        chain.doFilter(request, response);
          // Nếu đã refresh token, thêm access token mới vào response header
        if (tokenRefreshed) {
            httpResponse.setHeader("Authorization", "Bearer " + accessToken);
            // Cho phép client đọc header Authorization
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        }
    }    private boolean isPublicEndpoint(String method, String path) {
        // Các endpoint không cần xác thực
        return path.startsWith("/api/auth/") ||
               (path.equals("/api/products") || path.matches("/api/products/\\d+")) && method.equals("GET") ||
               path.equals("/api/categories") && method.equals("GET") ||
               path.equals("/api/authors") && method.equals("GET") ||
               path.equals("/api/publishers") && method.equals("GET") ||
               path.startsWith("/api/reviews") && method.equals("GET") ||
               // Cart creation - chỉ cho phép tạo cart mà không cần đăng nhập
               path.startsWith("/api/carts/create/") && method.equals("POST") ||
               // Temporarily allow admin endpoints for testing
               path.startsWith("/api/admin/") ||
               // API Documentation endpoints - comprehensive match
               path.startsWith("/swagger-ui") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               // Additional Swagger paths
               path.equals("/swagger-ui/index.html") ||
               path.contains("swagger-ui-bundle.js") ||
               path.contains("swagger-ui-standalone-preset.js");
    }

    private Role getUserRoleFromToken(String token) {
        try {
            String roleString = jwtUtil.extractRole(token);
            return Role.valueOf(roleString.toUpperCase());
        } catch (Exception e) {
            return Role.USER;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", message);
        errorResponse.put("status", status);
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
