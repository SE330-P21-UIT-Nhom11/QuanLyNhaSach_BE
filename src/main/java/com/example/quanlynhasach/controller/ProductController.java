package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.dto.ProductDTO;
import com.example.quanlynhasach.model.*;
import com.example.quanlynhasach.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final PublisherService publisherService;
    private final AuthorService authorService;

    public ProductController(ProductService productService, CategoryService categoryService,
            PublisherService publisherService, AuthorService authorService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
        this.authorService = authorService;
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
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        try {
            if (dto.getCategoryId() == null || dto.getPublisherId() == null) {
                return ResponseEntity.badRequest().body(null);
            }
            Category category = categoryService.getCategoryById(dto.getCategoryId());
            Publisher publisher = publisherService.getPublisherById(dto.getPublisherId());
            List<Author> authors = new ArrayList<>();
            if (dto.getAuthorIds() != null) {
                for (Integer authorId : dto.getAuthorIds()) {
                    Author author = authorService.getAuthorById(authorId);
                    if (author != null) {
                        authors.add(author);
                    }
                }
            }
            Product product = new Product(
                    dto.getTitle(),
                    dto.getSlug(),
                    dto.getPrice(),
                    dto.getDiscount(),
                    dto.getStock(),
                    dto.getDescription(),
                    dto.getCoverImage(),
                    publisher,
                    category,
                    authors);
            Product created = productService.createProduct(product);
            ProductDTO productdto = productService.convertToDTO(created);
            return ResponseEntity.status(201).body(productdto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cập nhật product
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable int id, @RequestBody ProductDTO dto) {
        try {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            if (dto.getTitle() != null)
                existingProduct.setTitle(dto.getTitle());
            if (dto.getSlug() != null)
                existingProduct.setSlug(dto.getSlug());
            if (dto.getPrice() != null)
                existingProduct.setPrice(dto.getPrice());
            if (dto.getDiscount() != null)
                existingProduct.setDiscount(dto.getDiscount());
            if (dto.getStock() != null)
                existingProduct.setStock(dto.getStock());
            if (dto.getDescription() != null)
                existingProduct.setDescription(dto.getDescription());
            if (dto.getCoverImage() != null)
                existingProduct.setCoverImage(dto.getCoverImage());

            if (dto.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(dto.getCategoryId());
                if (category != null) {
                    existingProduct.setCategory(category);
                }
            }

            if (dto.getPublisherId() != null) {
                Publisher publisher = publisherService.getPublisherById(dto.getPublisherId());
                if (publisher != null) {
                    existingProduct.setPublisher(publisher);
                }
            }

            if (dto.getAuthorIds() != null) {
                List<Author> authors = new ArrayList<>();
                for (Integer authorId : dto.getAuthorIds()) {
                    Author author = authorService.getAuthorById(authorId);
                    if (author != null) {
                        authors.add(author);
                    }
                }
                existingProduct.setAuthors(authors);
            }

            Product updatedProduct = productService.updateProduct(id, existingProduct);
            return ResponseEntity.ok(productService.convertToDTO(updatedProduct));
        } catch (Exception e) {
            e.printStackTrace();
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

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable int categoryId) {
        try {
            List<Product> products = productService.getProductsByCategoryId(categoryId);
            List<ProductDTO> productDTOs = products.stream().map(productService::convertToDTO).toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rating")
    public ResponseEntity<List<ProductDTO>> getTopRatedProducts() {
        try {
            List<Product> products = productService.getProductsByRating();
            List<ProductDTO> productDTOs = products.stream().map(productService::convertToDTO).toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/discount")
    public ResponseEntity<List<ProductDTO>> getProductsByDiscount() {
        try {
            List<Product> products = productService.getProductsSortedByDiscount();
            List<ProductDTO> productDTOs = products.stream().map(productService::convertToDTO).toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProductsByName(@RequestParam String name) {
        try {
            List<Product> products = productService.searchProductsByName(name);
            List<ProductDTO> productDTOs = products.stream().map(productService::convertToDTO).toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
