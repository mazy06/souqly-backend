package io.mazy.souqly_backend.dto;

public class CreateReviewRequest {
    private Long productId;
    private Long sellerId;
    private Integer rating;
    private String comment;
    private String transactionId;
    
    public CreateReviewRequest() {}
    
    public CreateReviewRequest(Long productId, Long sellerId, Integer rating, String comment, String transactionId) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.rating = rating;
        this.comment = comment;
        this.transactionId = transactionId;
    }
    
    // Getters et Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
} 