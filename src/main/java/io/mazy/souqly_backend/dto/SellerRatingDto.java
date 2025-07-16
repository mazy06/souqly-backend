package io.mazy.souqly_backend.dto;

import java.util.List;

public class SellerRatingDto {
    private double averageRating;
    private int totalReviews;
    private List<ReviewDto> recentReviews;

    public SellerRatingDto() {}

    public SellerRatingDto(double averageRating, int totalReviews, List<ReviewDto> recentReviews) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.recentReviews = recentReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public List<ReviewDto> getRecentReviews() {
        return recentReviews;
    }

    public void setRecentReviews(List<ReviewDto> recentReviews) {
        this.recentReviews = recentReviews;
    }
} 