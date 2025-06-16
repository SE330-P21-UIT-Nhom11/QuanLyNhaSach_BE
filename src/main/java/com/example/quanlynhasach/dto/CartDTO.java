package com.example.quanlynhasach.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CartDTO {
    private int id;
    private LocalDateTime createAt;
    private List<CartItemDTO> cartItems;

    public CartDTO() {
    }

    public CartDTO(int id, LocalDateTime createAt, List<CartItemDTO> cartItems) {
        this.id = id;
        this.createAt = createAt;
        this.cartItems = cartItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<CartItemDTO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }
}