package com.example.quanlynhasach.controller;

import com.example.quanlynhasach.model.Review;
import com.example.quanlynhasach.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        try {
            Review created = reviewService.createReview(review);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi tạo đánh giá: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Review> reviews = reviewService.getAllReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách đánh giá: " + e.getMessage());
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteReviewsByProduct(@PathVariable int productId) {
        try {
            boolean deleted = reviewService.deleteReviewsByProductId(productId);
            if (deleted) {
                return ResponseEntity.ok("Đã xóa đánh giá của sản phẩm ID = " + productId);
            } else {
                return ResponseEntity.status(404).body("Không có đánh giá nào để xóa.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa đánh giá: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable int id) {
        try {
            Review review = reviewService.getReviewById(id);
            if (review != null) {
                return ResponseEntity.ok(review);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy đánh giá: " + e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProductId(@PathVariable int productId) {
        try {
            List<Review> reviews = reviewService.getReviewsByProductId(productId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách đánh giá: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable int id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Đã xóa đánh giá có ID = " + id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa đánh giá: " + e.getMessage());
        }
    }
}