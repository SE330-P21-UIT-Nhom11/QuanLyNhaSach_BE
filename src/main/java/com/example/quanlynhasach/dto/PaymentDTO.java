package com.example.quanlynhasach.dto;

import com.example.quanlynhasach.model.Payment;

public class PaymentDTO {
    public int id;
    public int orderid;
    public String method;
    public String status;
    public String paidAt;
    public String address;
    public String phone;
    public String name;
    public String vouchercode;

    public PaymentDTO() {
    }

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.orderid = payment.getOrder().getId();
        this.method = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.paidAt = payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null;
        this.address = payment.getAddress();
        this.phone = payment.getPhone();
        this.name = payment.getName();
        this.vouchercode = payment.getVoucher() != null ? payment.getVoucher().getCode() : null;
    }
}