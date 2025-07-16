package io.mazy.souqly_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentIntentDto {
    private String id;
    private Double amount;
    private String currency;
    private String status; // "pending", "processing", "completed", "failed", "cancelled"
    private String paymentMethodId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 