package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId);

    // Lấy tất cả đơn hàng kèm chi tiết
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails")
    List<Order> findAllWithDetails();

    // Lấy đơn hàng theo userId kèm chi tiết
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.user.id = :userId")
    List<Order> findByUserIdWithDetails(@Param("userId") int userId);
}