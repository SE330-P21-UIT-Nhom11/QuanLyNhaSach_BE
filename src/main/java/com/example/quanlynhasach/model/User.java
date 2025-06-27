package com.example.quanlynhasach.model;

import com.example.quanlynhasach.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Cart cart;

    @Column(nullable = false, unique = true)
    private String name;

    private String email;
    private String password;
    private String phone;
    private String address;
    private double point = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER; // Default role is USER

    public User(String name, String email, String password, String phone, String address, Role role, double point) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.point = point;
    }

    // Constructor with String role for backward compatibility
    public User(String name, String email, String password, String phone, String address, String role, double point) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = Role.valueOf(role.toUpperCase());
        this.point = point;
    }

    // Methods for backward compatibility
    public String getRoleAsString() {
        return role != null ? role.getName() : Role.USER.getName();
    }

    public void setRoleFromString(String role) {
        this.role = Role.valueOf(role.toUpperCase());
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }
}