package com.example.quanlynhasach.config;

import com.example.quanlynhasach.middleware.AuthorizationMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private AuthorizationMiddleware authorizationMiddleware;

    @Bean
    public FilterRegistrationBean<AuthorizationMiddleware> authorizationFilter() {
        FilterRegistrationBean<AuthorizationMiddleware> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationMiddleware);
        registrationBean.addUrlPatterns("/api/*"); // Áp dụng cho tất cả API endpoints
        registrationBean.setOrder(2); // Sau authentication filter
        return registrationBean;
    }
}
