package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.model.Review;
import com.example.quanlynhasach.repository.ReviewRepository;
import com.example.quanlynhasach.service.ProductService;
import com.example.quanlynhasach.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductService productService;

    @Override
    public Review createReview(Review review) {
        Review saved = reviewRepository.save(review);
        productService.updateProductRating(saved.getProduct().getId());
        return saved;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByProductId(int productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Review getReviewById(int id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteReview(int id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            int productId = review.getProduct().getId();
            reviewRepository.deleteById(id);
            productService.updateProductRating(productId);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReviewsByProductId(int productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (!reviews.isEmpty()) {
            reviewRepository.deleteAll(reviews);
            productService.updateProductRating(productId);
            return true;
        }
        return false;
    }

}