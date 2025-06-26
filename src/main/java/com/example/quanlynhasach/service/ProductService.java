package com.example.quanlynhasach.service;

import com.example.quanlynhasach.dto.ProductDTO;
import com.example.quanlynhasach.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(int id);

    Product createProduct(Product product);

    Product updateProduct(int id, Product product);

    boolean deleteProduct(int id);

    public ProductDTO convertToDTO(Product product);

    void updateProductRating(int productId);

    List<Product> getProductsByCategoryId(int categoryId);

    List<Product> getProductsByRating();

    List<Product> getProductsSortedByDiscount();

    List<Product> searchProductsByName(String name);

}