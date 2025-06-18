package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.model.Product;
import com.example.quanlynhasach.dto.ProductDTO;
import com.example.quanlynhasach.model.Category;
import com.example.quanlynhasach.model.Publisher;
import com.example.quanlynhasach.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;

    public ProductController(ProductService productService, CategoryService categoryService,
            PublisherService publisherService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
    }

    // Lấy tất cả product
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDTO> productDTOs = products.stream()
                    .filter(p -> p.getCategory() != null && p.getPublisher() != null)
                    .map(productService::convertToDTO)
                    .toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Lấy product theo id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int id) {
        try {
            Product product = productService.getProductById(id);
            if (product != null) {
                ProductDTO dto = productService.convertToDTO(product);
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Tạo product mới
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO dto) {
        try {
            if (dto.getCategoryId() <= 0 || dto.getPublisherId() <= 0) {
                return ResponseEntity.badRequest().body(null);
            }
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            Publisher publisher = publisherService.getPublisherById(dto.getPublisherId());
            Product product = new Product(
                    dto.getTitle(),
                    dto.getSlug(),
                    dto.getPrice(),
                    dto.getDiscount(),
                    dto.getStock(),
                    dto.getDescription(),
                    dto.getCoverImage(),
                    publisher,
                    category);
            Product created = productService.createProduct(product);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cập nhật product
    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Xóa product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa sản phẩm có ID = " + id);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
