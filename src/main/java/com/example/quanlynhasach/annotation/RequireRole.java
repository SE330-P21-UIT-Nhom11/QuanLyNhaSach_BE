package com.example.quanlynhasach.annotation;

import com.example.quanlynhasach.model.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    Role[] value();
    
    /**
     * Nếu true, user cần có TẤT CẢ roles
     * Nếu false, user chỉ cần có ÍT NHẤT MỘT role
     */
    boolean requireAll() default false;
}
