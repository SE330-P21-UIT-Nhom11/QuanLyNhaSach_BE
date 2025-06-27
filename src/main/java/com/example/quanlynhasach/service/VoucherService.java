package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.Voucher;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    Voucher createVoucher(Voucher voucher);

    Voucher getVoucherById(int id);

    Voucher getVoucherByCode(String code);

    List<Voucher> getAllVouchers();

    Voucher updateVoucher(int id, Voucher updatedVoucher);

    boolean deleteVoucher(int id);

    List<Voucher> getValidVoucher(int userPoint, BigDecimal totalAmount);
}