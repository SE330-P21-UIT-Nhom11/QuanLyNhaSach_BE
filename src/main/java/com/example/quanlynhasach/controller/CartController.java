package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.model.Cart;
import com.example.quanlynhasach.model.CartItem;
import com.example.quanlynhasach.dto.*;
import com.example.quanlynhasach.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Lấy giỏ hàng của user
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUser(@PathVariable int userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            return ResponseEntity.ok(cartService.convertToDTO(cart));
        }
        return ResponseEntity.notFound().build();
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/{userId}/add")
    public ResponseEntity<String> addItemToCart(@PathVariable int userId,
            @RequestParam int productId,
            @RequestParam int quantity) {
        try {
            cartService.addItemToCart(userId, productId, quantity);
            return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<String> removeItemFromCart(@PathVariable int userId, @PathVariable int productId) {
        try {
            cartService.removeItemFromCart(userId, productId);
            return ResponseEntity.ok("Sản phẩm đã được xóa khỏi giỏ hàng!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Làm sạch giỏ hàng
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable int userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("Giỏ hàng đã được làm sạch!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy danh sách các mục trong giỏ hàng
    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItemDTO>> getCartItems(@PathVariable int cartId) {
        List<CartItem> items = cartService.getCartItems(cartId);
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cartService.convertCartItemDTOList(items));
    }

    // Xóa giỏ hàng
    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable int cartId) {
        boolean deleted = cartService.deleteCart(cartId);
        if (deleted) {
            return ResponseEntity.ok("Giỏ hàng đã được xóa thành công!");
        }
        return ResponseEntity.notFound().build();
    }
}