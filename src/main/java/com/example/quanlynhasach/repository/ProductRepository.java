package com.example.quanlynhasach.repository;

import com.example.quanlynhasach.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findBytitle(String title);

    List<Product> findByCategoryId(int categoryId);

    List<Product> findAllByOrderByDiscountDesc();

    List<Product> findByTitleContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p ORDER BY p.rating DESC")
    List<Product> findTopRatedProducts();

}