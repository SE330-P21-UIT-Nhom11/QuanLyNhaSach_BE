package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValue(String tokenValue);

    Optional<Token> findByUserId(Long userId);
}
