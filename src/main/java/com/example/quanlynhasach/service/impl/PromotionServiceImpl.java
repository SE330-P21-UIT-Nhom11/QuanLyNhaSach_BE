package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.model.Promotion;
import com.example.quanlynhasach.model.enums.Rank;
import com.example.quanlynhasach.repository.PromotionRepository;
import com.example.quanlynhasach.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public Promotion createPromotion(Promotion promotion) {
        if (promotionRepository.existsByRank(promotion.getRank())) {
            throw new IllegalArgumentException("Rank " + promotion.getRank() + " already exists.");
        }
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getPromotionByRank(Rank rank) {
        return promotionRepository.findByRank(rank).orElse(null);
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion updatePromotion(Rank rank, Promotion updated) {
        return promotionRepository.findByRank(rank).map(p -> {
            p.setPoint(updated.getPoint());
            p.setTierMultiplier(updated.getTierMultiplier());
            return promotionRepository.save(p);
        }).orElse(null);
    }

    @Override
    public boolean deletePromotion(Rank rank) {
        return promotionRepository.findByRank(rank).map(p -> {
            promotionRepository.delete(p);
            return true;
        }).orElse(false);
    }
}