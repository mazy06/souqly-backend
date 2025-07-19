package io.mazy.souqly_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_interactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;
    
    @Column(name = "interaction_value")
    private String interactionValue;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum InteractionType {
        VIEW,           // Vue d'un produit
        FAVORITE,       // Ajout aux favoris
        UNFAVORITE,     // Retrait des favoris
        SEARCH,         // Recherche
        CLICK,          // Clic sur un produit
        SHARE,          // Partage
        CONTACT,        // Contact du vendeur
        PURCHASE,       // Achat
        RATE,           // Note/évaluation
        COMMENT         // Commentaire
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 