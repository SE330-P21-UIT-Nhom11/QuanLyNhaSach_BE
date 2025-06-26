package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.model.Promotion;
import com.example.quanlynhasach.model.enums.Rank;
import com.example.quanlynhasach.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        try {
            Promotion created = promotionService.createPromotion(promotion);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAll() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/{rank}")
    public ResponseEntity<Promotion> getByRank(@PathVariable Rank rank) {
        Promotion promotion = promotionService.getPromotionByRank(rank);
        return promotion != null ? ResponseEntity.ok(promotion) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{rank}")
    public ResponseEntity<Promotion> update(@PathVariable Rank rank, @RequestBody Promotion updated) {
        Promotion result = promotionService.updatePromotion(rank, updated);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{rank}")
    public ResponseEntity<Void> delete(@PathVariable Rank rank) {
        return promotionService.deletePromotion(rank) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}