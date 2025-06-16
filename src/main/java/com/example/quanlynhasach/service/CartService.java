package com.example.quanlynhasach.service;

import com.example.quanlynhasach.dto.CartDTO;
import com.example.quanlynhasach.dto.CartItemDTO;
import com.example.quanlynhasach.model.Cart;
import com.example.quanlynhasach.model.CartItem;
import com.example.quanlynhasach.model.User;

import java.util.List;

public interface CartService {

    Cart createCart(User user);

    Cart getCartByUserId(int userId);

    Cart getCartById(int id);

    boolean deleteCart(int id);

    List<CartItem> getCartItems(int cartId);

    void addItemToCart(int userId, int productId, int quantity);

    void removeItemFromCart(int userId, int productId);

    void clearCart(int userId);

    CartDTO convertToDTO(Cart cart);

    List<CartItemDTO> convertCartItemDTOList(List<CartItem> cartItems);
}
