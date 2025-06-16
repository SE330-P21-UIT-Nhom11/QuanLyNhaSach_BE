package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.dto.*;
import com.example.quanlynhasach.model.*;
import com.example.quanlynhasach.repository.CartItemRepository;
import com.example.quanlynhasach.repository.CartRepository;
import com.example.quanlynhasach.repository.ProductRepository;
import com.example.quanlynhasach.repository.UserRepository;
import com.example.quanlynhasach.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart(user, LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUserId(int userId) {
        return cartRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Cart getCartById(int id) {
        return cartRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteCart(int id) {
        Cart cart = getCartById(id);
        if (cart != null) {
            cartRepository.delete(cart);
            return true;
        }
        return false;
    }

    @Override
    public List<CartItem> getCartItems(int cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Override
    public void addItemToCart(int userId, int productId, int quantity) {
        // Lấy giỏ hàng của user
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                throw new RuntimeException("Người dùng không tồn tại!");
            }
            cart = createCart(user);
        }

        // Kiểm tra sản phẩm
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("Sản phẩm không tồn tại!");
        }

        // Tạo khóa và cập nhật số lượng sản phẩm trong giỏ hàng
        CartItemKey key = new CartItemKey(cart.getId(), product.getId());
        CartItem item = cartItemRepository.findById(key).orElse(null);
        if (item == null) {
            item = new CartItem(cart, product, 0);
        }

        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);
    }

    @Override
    public void removeItemFromCart(int userId, int productId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Giỏ hàng không tồn tại!");
        }
        CartItemKey key = new CartItemKey(cart.getId(), productId);
        cartItemRepository.deleteById(key);
    }

    @Override
    public void clearCart(int userId) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Giỏ hàng không tồn tại!");
        }
        cartItemRepository.deleteByCartId(cart.getId());
    }

    @Override
    public CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setCreateAt(cart.getCreateAt());

        List<CartItemDTO> items = cart.getCartItems().stream()
                .map(cartItem -> new CartItemDTO(
                        cart.getId(),
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getTitle(),
                        cartItem.getQuantity()))
                .collect(Collectors.toList());

        dto.setCartItems(items);
        return dto;
    }

    @Override
    public List<CartItemDTO> convertCartItemDTOList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> new CartItemDTO(
                        cartItem.getCart().getId(),
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getTitle(),
                        cartItem.getQuantity()))
                .collect(Collectors.toList());
    }

}