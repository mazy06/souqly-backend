package io.mazy.souqly_backend.dto;

import java.time.LocalDateTime;

public class ReviewDto {
    private Long id;
    private Long productId;
    private Long sellerId;
    private Long buyerId;
    private Integer rating;
    private String comment;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Informations supplémentaires
    private String productTitle;
    private String sellerName;
    private String buyerName;
    
    public ReviewDto() {}
    
    public ReviewDto(Long id, Long productId, Long sellerId, Long buyerId, Integer rating, 
                    String comment, String transactionId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.rating = rating;
        this.comment = comment;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Long getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getProductTitle() {
        return productTitle;
    }
    
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    
    public String getBuyerName() {
        return buyerName;
    }
    
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }
} 