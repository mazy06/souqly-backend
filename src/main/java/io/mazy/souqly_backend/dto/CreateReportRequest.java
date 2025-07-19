package io.mazy.souqly_backend.dto;

import java.util.List;

public class CreateReportRequest {
    private Long productId;
    private Long userId;
    private List<String> reasons;
    private String customReason;
    private String description;

    // Constructeurs
    public CreateReportRequest() {}

    public CreateReportRequest(Long productId, Long userId, List<String> reasons) {
        this.productId = productId;
        this.userId = userId;
        this.reasons = reasons;
    }

    // Getters et Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 