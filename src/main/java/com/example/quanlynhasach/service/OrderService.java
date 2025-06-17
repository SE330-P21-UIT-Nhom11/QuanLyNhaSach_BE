package com.example.quanlynhasach.service;

import com.example.quanlynhasach.dto.OrderDTO;
import com.example.quanlynhasach.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();

    Optional<Order> getOrderById(int id);

    List<Order> getOrdersByUserId(int userId);

    Order createOrder(Order order);

    boolean deleteOrder(int id);

    OrderDTO convertToDTO(Order order);

    Order createOrderFromDTO(OrderDTO dto);

}
