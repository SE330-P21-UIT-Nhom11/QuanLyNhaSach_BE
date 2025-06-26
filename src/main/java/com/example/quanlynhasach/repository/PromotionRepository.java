package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Promotion;
import com.example.quanlynhasach.model.enums.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    Optional<Promotion> findByRank(Rank rank);

    boolean existsByRank(Rank rank);
}