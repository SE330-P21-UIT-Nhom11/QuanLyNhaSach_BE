package com.example.quanlynhasach.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartItemKey implements Serializable {

    @Column(name = "cart_id")
    private int cartId;

    @Column(name = "product_id")
    private int productId;

    public CartItemKey() {
    }

    public CartItemKey(int cartId, int productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CartItemKey))
            return false;
        CartItemKey that = (CartItemKey) o;
        return cartId == that.cartId &&
                productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }
}