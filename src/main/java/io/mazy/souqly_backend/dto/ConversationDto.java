package io.mazy.souqly_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    private String id;
    private String name;
    private String avatarUrl;
    private String lastMessage;
    private String time;
    private Integer unreadCount;
    private Long productId;
    private Long sellerId;
    private String productTitle;
    private String productImageUrl;
    private LocalDateTime lastMessageTime;
    // Ajouté pour le frontend
    private String buyerName;
    private String sellerName;
} 