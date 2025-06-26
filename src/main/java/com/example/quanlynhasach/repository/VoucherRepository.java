package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    Voucher findByCode(String code);
}