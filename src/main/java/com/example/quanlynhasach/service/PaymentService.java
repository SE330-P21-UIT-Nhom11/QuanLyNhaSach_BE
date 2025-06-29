package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();

    Payment getPaymentById(int id);

    Payment getPaymentByOrderId(int id);

    Payment createPayment(Payment payment);

    Payment updatePayment(int id, Payment payment);

    boolean deletePayment(int id);
}