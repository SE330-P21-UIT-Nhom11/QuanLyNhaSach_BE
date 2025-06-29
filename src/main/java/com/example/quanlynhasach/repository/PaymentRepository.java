package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Payment findByOrderId(int orderId);
}