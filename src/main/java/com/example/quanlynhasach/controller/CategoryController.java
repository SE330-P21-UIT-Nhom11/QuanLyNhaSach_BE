package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.model.Category;
import com.example.quanlynhasach.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả category
    @GetMapping
    public ResponseEntity<?> getAllCategorys() {
        try {
            List<Category> categorys = categoryService.getAllCategorys();
            return ResponseEntity.ok(categorys);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi lấy thể loại: " + e.getMessage());
        }
    }

    // Lấy category theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable int id) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thể loại có id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi lấy thể loại: " + e.getMessage());
        }
    }

    // Tạo category mới
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category created = categoryService.createCategory(category);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Cập nhật category
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            if (updatedCategory != null) {
                return ResponseEntity.ok(updatedCategory);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thể loại có id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Xóa category
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
        try {
            boolean deleted = categoryService.deleteCategory(id);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa tác giả có ID = " + id);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy thể loại có id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}