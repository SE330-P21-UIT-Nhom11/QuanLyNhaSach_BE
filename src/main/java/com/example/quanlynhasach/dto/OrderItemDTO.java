package com.example.quanlynhasach.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discount;

    public OrderItemDTO() {
    }

    public OrderItemDTO(int productId, String productName, int quantity, BigDecimal price, BigDecimal discount) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}