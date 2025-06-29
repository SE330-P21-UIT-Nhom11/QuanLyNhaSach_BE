package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.model.Payment;
import com.example.quanlynhasach.repository.PaymentRepository;
import com.example.quanlynhasach.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(int id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public Payment getPaymentByOrderId(int orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePayment(int id, Payment payment) {
        return paymentRepository.findById(id).map(existing -> {
            if (payment.getOrder() != null) {
                existing.setOrder(payment.getOrder());
            }
            if (payment.getPaymentMethod() != null) {
                existing.setPaymentMethod(payment.getPaymentMethod());
            }
            if (payment.getStatus() != null) {
                existing.setStatus(payment.getStatus());
            }
            if (payment.getPaymentDate() != null) {
                existing.setPaymentDate(payment.getPaymentDate());
            }
            if (payment.getVoucher() != null) {
                existing.setVoucher(payment.getVoucher());
            }
            if (payment.getAddress() != null) {
                existing.setAddress(payment.getAddress());
            }
            if (payment.getPhone() != null) {
                existing.setPhone(payment.getPhone());
            }
            if (payment.getName() != null) {
                existing.setName(payment.getName());
            }

            return paymentRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public boolean deletePayment(int id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}