package com.example.quanlynhasach.model;

import com.example.quanlynhasach.model.enums.Role;
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

    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER; // Default role is USER

    public User(String name, String email, String password, String phone, String address, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    // Constructor with String role for backward compatibility
    public User(String name, String email, String password, String phone, String address, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = Role.valueOf(role.toUpperCase());
    }

    // Methods for backward compatibility
    public String getRoleAsString() {
        return role != null ? role.getName() : Role.USER.getName();
    }

    public void setRoleFromString(String role) {
        this.role = Role.valueOf(role.toUpperCase());
    }
}