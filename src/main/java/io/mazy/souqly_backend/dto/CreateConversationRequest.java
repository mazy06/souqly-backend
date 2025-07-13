package io.mazy.souqly_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    private Long sellerId;
    private Long productId;
    private String initialMessage;
    private Double offerPrice;
} 