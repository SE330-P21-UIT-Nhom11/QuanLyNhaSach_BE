package com.example.quanlynhasach.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "orderid", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String method;

    private String status;
    private LocalDateTime paidAt;
    private String address;
    private String phone;
    private String name;
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "voucherid")
    private Voucher voucher;

    // Constructors
    public Payment() {
    }

    public Payment(Order order, String method, String status, LocalDateTime paidAt, String address, String phone,
            String name, BigDecimal totalAmount, Voucher voucher) {
        this.order = order;
        this.method = method;
        this.status = status;
        this.paidAt = paidAt;
        this.address = address;
        this.phone = phone;
        this.name = name;
        this.voucher = voucher;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getPaymentMethod() {
        return method;
    }

    public void setPaymentMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paidAt;
    }

    public void setPaymentDate(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}