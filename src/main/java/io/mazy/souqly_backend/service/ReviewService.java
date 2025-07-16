package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.Review;
import io.mazy.souqly_backend.repository.ReviewRepository;
import io.mazy.souqly_backend.dto.ReviewDto;
import io.mazy.souqly_backend.dto.SellerRatingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    public List<ReviewDto> getReviewsBySeller(Long sellerId) {
        List<Review> reviews = reviewRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SellerRatingDto getSellerRating(Long sellerId) {
        List<Review> reviews = reviewRepository.findBySellerId(sellerId);
        
        if (reviews.isEmpty()) {
            return new SellerRatingDto(0.0, 0, List.of());
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        List<ReviewDto> recentReviews = reviews.stream()
                .limit(5) // Limiter aux 5 derniers commentaires
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new SellerRatingDto(averageRating, reviews.size(), recentReviews);
    }

    public ReviewDto createReview(ReviewDto reviewDto) {
        Review review = new Review(
            reviewDto.getProductId(),
            reviewDto.getSellerId(),
            reviewDto.getBuyerId(),
            reviewDto.getRating(),
            reviewDto.getComment(),
            reviewDto.getTransactionId()
        );
        
        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setProductId(review.getProductId());
        dto.setSellerId(review.getSellerId());
        dto.setBuyerId(review.getBuyerId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setTransactionId(review.getTransactionId());
        dto.setCreatedAt(review.getCreatedAt());
        
        // Récupérer les informations de l'acheteur
        if (review.getBuyer() != null) {
            dto.setBuyerName(review.getBuyer().getFirstName() + " " + review.getBuyer().getLastName());
        }
        
        return dto;
    }
} 