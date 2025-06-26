package com.example.quanlynhasach.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "discount_type", nullable = false)
    private String discountType; // "fixed" hoặc "percent"

    @Column(nullable = false)
    private BigDecimal value;

    @Column(name = "max_usage", nullable = false)
    private int maxUsage;

    @Column(nullable = false)
    private int remaining;

    @Column(name = "min_purchase", nullable = false)
    private BigDecimal minPurchase;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public Voucher() {
    }

    public Voucher(String code, String discountType, BigDecimal value, int maxUsage, int remaining,
            BigDecimal minPurchase, LocalDateTime expiryDate) {
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.maxUsage = maxUsage;
        this.remaining = remaining;
        this.minPurchase = minPurchase;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public BigDecimal getMinPurchase() {
        return minPurchase;
    }

    public void setMinPurchase(BigDecimal minPurchase) {
        this.minPurchase = minPurchase;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}