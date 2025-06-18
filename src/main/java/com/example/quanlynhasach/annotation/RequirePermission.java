package com.example.quanlynhasach.annotation;

import com.example.quanlynhasach.model.enums.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    Permission[] value();
    
    /**
     * Nếu true, user cần có TẤT CẢ permissions
     * Nếu false, user chỉ cần có ÍT NHẤT MỘT permission
     */
    boolean requireAll() default true;
}
