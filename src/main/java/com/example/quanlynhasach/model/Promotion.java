package com.example.quanlynhasach.model;

import com.example.quanlynhasach.model.enums.Rank;
import jakarta.persistence.*;

@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Rank rank;

    @Column(nullable = false)
    private float point;

    @Column(name = "tier_multiplier", nullable = false)
    private float tierMultiplier;

    public Promotion() {
    }

    public Promotion(Rank rank, float point, float tierMultiplier) {
        this.rank = rank;
        this.point = point;
        this.tierMultiplier = tierMultiplier;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public float getTierMultiplier() {
        return tierMultiplier;
    }

    public void setTierMultiplier(float tierMultiplier) {
        this.tierMultiplier = tierMultiplier;
    }
}