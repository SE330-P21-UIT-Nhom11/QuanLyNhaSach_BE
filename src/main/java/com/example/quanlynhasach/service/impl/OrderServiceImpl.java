package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.dto.OrderDTO;
import com.example.quanlynhasach.dto.OrderItemDTO;
import com.example.quanlynhasach.model.Order;
import com.example.quanlynhasach.model.OrderDetail;
import com.example.quanlynhasach.model.Product;
import com.example.quanlynhasach.model.User;
import com.example.quanlynhasach.repository.OrderDetailRepository;
import com.example.quanlynhasach.repository.OrderRepository;
import com.example.quanlynhasach.repository.ProductRepository;
import com.example.quanlynhasach.repository.UserRepository;
import com.example.quanlynhasach.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order createOrder(Order order) {
        int userId = order.getUser().getId();
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        order.setUser(userOpt.get());
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return order;
    }

    @Override
    public boolean deleteOrder(int id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> items = order.getOrderDetails().stream()
                .map(detail -> new OrderItemDTO(
                        detail.getProduct().getId(),
                        detail.getProduct().getTitle(),
                        detail.getQuantity(),
                        detail.getPrice(),
                        detail.getProduct().getDiscount()))
                .toList();

        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                items);
    }

    @Override
    public Order createOrderFromDTO(OrderDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDateTime.now());
        order.setStatus(dto.getStatus());
        order.setTotalAmount(BigDecimal.valueOf(0));
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (OrderItemDTO item : dto.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product không tồn tại: " + item.getProductId()));

            BigDecimal price = product.getPrice();
            BigDecimal discount = product.getDiscount() != null ? product.getDiscount() : BigDecimal.ZERO;
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100)));
            BigDecimal finalPrice = price.multiply(discountMultiplier);

            total = total.add(finalPrice.multiply(BigDecimal.valueOf(item.getQuantity())));

            OrderDetail detail = new OrderDetail(order, product, item.getQuantity(), finalPrice);
            orderDetails.add(detail);
        }

        // Chỉ sau khi tính xong total và detail, mới set vào Order
        order.setTotalAmount(total);
        order.setOrderDetails(orderDetails);

        // Lưu order trước (vì OrderDetail cần khóa ngoại order_id)
        order = orderRepository.save(order);

        // Lưu chi tiết đơn hàng
        orderDetailRepository.saveAll(orderDetails);

        return order;
    }

}