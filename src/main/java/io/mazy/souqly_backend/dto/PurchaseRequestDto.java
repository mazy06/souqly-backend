package io.mazy.souqly_backend.dto;

import lombok.Data;

@Data
public class PurchaseRequestDto {
    private Long productId;
    private String paymentMethodId;
    private Double amount;
    private ShippingAddressDto shippingAddress;
}

@Data
class ShippingAddressDto {
    private String street;
    private String city;
    private String postalCode;
    private String country;
} 