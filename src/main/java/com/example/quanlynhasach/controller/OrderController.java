package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.OrderDTO;
import com.example.quanlynhasach.model.Order;
import com.example.quanlynhasach.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            List<OrderDTO> dtos = orders.stream()
                    .map(orderService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        try {
            return orderService.getOrderById(id)
                    .<ResponseEntity<?>>map(order -> ResponseEntity.ok(orderService.convertToDTO(order)))
                    .orElse(ResponseEntity.status(404).body("Không tìm thấy đơn hàng với ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy đơn hàng: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable int userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            List<OrderDTO> dtos = orders.stream()
                    .map(orderService::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy đơn hàng theo người dùng: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO dto) {
        try {
            Order createdOrder = orderService.createOrderFromDTO(dto);
            OrderDTO responseDTO = orderService.convertToDTO(createdOrder);
            return ResponseEntity.status(201).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Yêu cầu không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int orderId,
            @RequestParam String status) {
        try {
            Order order = orderService.updateOrderStatus(orderId, status);
            if (order != null) {
                return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công.");
            } else {
                return ResponseEntity.status(404)
                        .body("Không tìm thấy đơn hàng với ID: " + orderId);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable int id) {
        try {
            boolean deleted = orderService.deleteOrder(id);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa đơn hàng có ID = " + id);
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy đơn hàng để xóa với ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa đơn hàng: " + e.getMessage());
        }
    }
}