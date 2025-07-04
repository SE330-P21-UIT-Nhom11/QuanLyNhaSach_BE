package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.model.Voucher;
import com.example.quanlynhasach.repository.VoucherRepository;
import com.example.quanlynhasach.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public Voucher createVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher getVoucherById(int id) {
        return voucherRepository.findById(id).orElse(null);
    }

    @Override
    public Voucher getVoucherByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    public Voucher updateVoucher(int id, Voucher updatedVoucher) {
        return voucherRepository.findById(id).map(v -> {
            if (updatedVoucher.getCode() != null)
                v.setCode(updatedVoucher.getCode());

            if (updatedVoucher.getDiscountType() != null)
                v.setDiscountType(updatedVoucher.getDiscountType());

            if (updatedVoucher.getValue() != null)
                v.setValue(updatedVoucher.getValue());

            if (updatedVoucher.getMaxUsage() != 0)
                v.setMaxUsage(updatedVoucher.getMaxUsage());

            if (updatedVoucher.getRemaining() != 0)
                v.setRemaining(updatedVoucher.getRemaining());

            if (updatedVoucher.getMinPurchase() != null)
                v.setMinPurchase(updatedVoucher.getMinPurchase());

            if (updatedVoucher.getExpiryDate() != null)
                v.setExpiryDate(updatedVoucher.getExpiryDate());

            if (updatedVoucher.getPoint() != null) {
                v.setPoint(updatedVoucher.getPoint());
            }

            return voucherRepository.save(v);
        }).orElse(null);
    }

    @Override
    public boolean deleteVoucher(int id) {
        if (voucherRepository.existsById(id)) {
            voucherRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Voucher> getValidVoucher(int userPoint, BigDecimal totalAmount) {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> vouchers = voucherRepository.findAll();
        List<Voucher> result = new ArrayList<>();

        for (Voucher voucher : vouchers) {
            boolean valid = voucher.getRemaining() > 0 &&
                    now.isBefore(voucher.getExpiryDate()) &&
                    totalAmount.compareTo(voucher.getMinPurchase()) >= 0 &&
                    userPoint >= voucher.getPoint();

            if (valid) {
                result.add(voucher);
            }
        }

        return result;
    }

}