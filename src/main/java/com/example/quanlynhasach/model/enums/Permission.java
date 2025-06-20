package com.example.quanlynhasach.model.enums;

public enum Permission {
    // Product Permissions
    PRODUCT_READ("product:read"),
    PRODUCT_WRITE("product:write"),
    PRODUCT_DELETE("product:delete"),
    
    // User Permissions
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_DELETE("user:delete"),
    
    // Order Permissions
    ORDER_READ("order:read"),
    ORDER_WRITE("order:write"),
    ORDER_DELETE("order:delete"),
    ORDER_READ_ALL("order:read:all"),
    
    // Category Permissions
    CATEGORY_READ("category:read"),
    CATEGORY_WRITE("category:write"),
    CATEGORY_DELETE("category:delete"),
    
    // Author Permissions
    AUTHOR_READ("author:read"),
    AUTHOR_WRITE("author:write"),
    AUTHOR_DELETE("author:delete"),
    
    // Publisher Permissions
    PUBLISHER_READ("publisher:read"),
    PUBLISHER_WRITE("publisher:write"),
    PUBLISHER_DELETE("publisher:delete"),
    
    // Cart Permissions
    CART_READ("cart:read"),
    CART_WRITE("cart:write"),
    
    // Review Permissions
    REVIEW_READ("review:read"),
    REVIEW_WRITE("review:write"),
    REVIEW_DELETE("review:delete"),
    REVIEW_DELETE_ANY("review:delete:any"),
    
    // Payment Permissions
    PAYMENT_READ("payment:read"),
    PAYMENT_WRITE("payment:write"),
    PAYMENT_READ_ALL("payment:read:all"),
    
    // Admin Permissions
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    ADMIN_DELETE("admin:delete");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return permission;
    }
}
