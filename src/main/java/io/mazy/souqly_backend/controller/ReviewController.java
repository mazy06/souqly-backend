package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.ReviewDto;
import io.mazy.souqly_backend.dto.SellerRatingDto;
import io.mazy.souqly_backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Récupère tous les commentaires d'un vendeur
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ReviewDto>> getSellerReviews(@PathVariable Long sellerId) {
        List<ReviewDto> reviews = reviewService.getReviewsBySeller(sellerId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Récupère la note moyenne et les commentaires récents d'un vendeur
     */
    @GetMapping("/seller/{sellerId}/rating")
    public ResponseEntity<SellerRatingDto> getSellerRating(@PathVariable Long sellerId) {
        SellerRatingDto rating = reviewService.getSellerRating(sellerId);
        return ResponseEntity.ok(rating);
    }

    /**
     * Crée un nouveau commentaire
     */
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        ReviewDto createdReview = reviewService.createReview(reviewDto);
        return ResponseEntity.ok(createdReview);
    }
} 