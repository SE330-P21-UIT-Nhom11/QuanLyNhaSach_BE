package com.example.quanlynhasach.service;

import com.example.quanlynhasach.model.Promotion;
import com.example.quanlynhasach.model.enums.Rank;

import java.util.List;

public interface PromotionService {
    Promotion createPromotion(Promotion promotion);

    Promotion getPromotionByRank(Rank rank);

    List<Promotion> getAllPromotions();

    Promotion updatePromotion(Rank rank, Promotion updated);

    boolean deletePromotion(Rank rank);
}