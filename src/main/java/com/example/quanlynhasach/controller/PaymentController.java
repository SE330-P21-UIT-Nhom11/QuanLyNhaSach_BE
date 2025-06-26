package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.model.Payment;
import com.example.quanlynhasach.model.Order;
import com.example.quanlynhasach.dto.PaymentDTO;
import com.example.quanlynhasach.model.Voucher;
import com.example.quanlynhasach.repository.VoucherRepository;
import com.example.quanlynhasach.repository.OrderRepository;
import com.example.quanlynhasach.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private OrderRepository orderRepository;

    // GET all payments
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách thanh toán: " + e.getMessage());
        }
    }

    // GET payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable int id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            if (payment != null) {
                return ResponseEntity.ok(payment);
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy thanh toán với ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy thanh toán: " + e.getMessage());
        }
    }

    // POST: Create new payment
    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentDTO dto) {
        try {
            // Tìm order
            Order order = orderRepository.findById(dto.orderid).orElse(null);
            if (order == null) {
                return ResponseEntity.status(400).body("Không tìm thấy đơn hàng.");
            }

            // Tìm voucher (nếu có)
            Voucher voucher = null;
            if (dto.vouchercode != null) {
                voucher = voucherRepository.findByCode(dto.vouchercode);
                if (voucher == null) {
                    return ResponseEntity.status(400).body("Voucher không hợp lệ.");
                }
            }

            // Parse ngày
            LocalDateTime paidAt = LocalDateTime.parse(dto.paidAt);

            // Tạo Payment
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentMethod(dto.method);
            payment.setStatus(dto.status);
            payment.setPaymentDate(paidAt);
            payment.setAddress(dto.address);
            payment.setPhone(dto.phone);
            payment.setName(dto.name);
            payment.setVoucher(voucher);

            return ResponseEntity.status(201).body(paymentService.createPayment(payment));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi tạo thanh toán: " + e.getMessage());
        }
    }

    // PATCH: Update payment
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable int id, @RequestBody PaymentDTO dto) {
        try {
            Payment existing = paymentService.getPaymentById(id);
            if (existing == null) {
                return ResponseEntity.status(404).body("Không tìm thấy thanh toán để cập nhật với ID: " + id);
            }

            // Cập nhật voucher nếu có
            if (dto.vouchercode != null) {
                Voucher voucher = voucherRepository.findByCode(dto.vouchercode);
                if (voucher == null) {
                    return ResponseEntity.badRequest().body("Không tìm thấy voucher với mã: " + dto.vouchercode);
                }
                existing.setVoucher(voucher);
            }

            // Các trường còn lại
            if (dto.method != null)
                existing.setPaymentMethod(dto.method);
            if (dto.status != null)
                existing.setStatus(dto.status);
            if (dto.paidAt != null)
                existing.setPaymentDate(LocalDateTime.parse(dto.paidAt));
            if (dto.address != null)
                existing.setAddress(dto.address);
            if (dto.phone != null)
                existing.setPhone(dto.phone);
            if (dto.name != null)
                existing.setName(dto.name);

            return ResponseEntity.ok(paymentService.createPayment(existing)); // dùng createPayment để save lại

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi cập nhật thanh toán: " + e.getMessage());
        }
    }

    // DELETE: Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable int id) {
        try {
            boolean deleted = paymentService.deletePayment(id);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa thanh toán với ID = " + id);
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy thanh toán để xóa với ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa thanh toán: " + e.getMessage());
        }
    }
}