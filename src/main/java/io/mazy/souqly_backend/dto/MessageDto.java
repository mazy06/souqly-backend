package io.mazy.souqly_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String id;
    private String text;
    private String timestamp;
    private Boolean isFromMe;
    private Long productId;
    private Double offerPrice;
    private String sender;
    private String conversationId;
    private LocalDateTime sentAt;
} 