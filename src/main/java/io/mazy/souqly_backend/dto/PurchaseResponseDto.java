package io.mazy.souqly_backend.dto;

import lombok.Data;

@Data
public class PurchaseResponseDto {
    private boolean success;
    private String transactionId;
    private PaymentIntentDto paymentIntent;
    private String message;
} 