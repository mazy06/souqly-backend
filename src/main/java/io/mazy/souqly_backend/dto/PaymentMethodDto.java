package io.mazy.souqly_backend.dto;

import lombok.Data;

@Data
public class PaymentMethodDto {
    private String id;
    private String type; // "card", "wallet", "bank_transfer"
    private String name;
    private String last4;
    private String brand;
    private boolean isDefault;
    private String cardholderName;
    private String expiryMonth;
    private String expiryYear;
} 