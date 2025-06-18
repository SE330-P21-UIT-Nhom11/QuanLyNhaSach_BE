package com.example.quanlynhasach.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 30 * 24 * 60 * 60; // 30 days in seconds

    /**
     * Tạo refresh token cookie
     */
    public static void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true); // Không thể truy cập từ JavaScript (bảo mật)
        cookie.setSecure(false); // Set true khi deploy với HTTPS
        cookie.setPath("/"); // Cookie có hiệu lực trên toàn bộ domain
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
        
        response.addCookie(cookie);
    }

    /**
     * Lấy refresh token từ cookie
     */
    public static String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Xóa refresh token cookie (logout)
     */
    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set true khi deploy với HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Xóa cookie
        
        response.addCookie(cookie);
    }

    /**
     * Kiểm tra cookie có tồn tại không
     */
    public static boolean hasRefreshTokenCookie(HttpServletRequest request) {
        return getRefreshTokenFromCookie(request) != null;
    }
}
