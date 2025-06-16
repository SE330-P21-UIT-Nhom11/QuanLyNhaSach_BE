package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Cart;
import com.example.quanlynhasach.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserId(int userId);

    boolean existsByUser(User user);

    void deleteByUser(User user);
}