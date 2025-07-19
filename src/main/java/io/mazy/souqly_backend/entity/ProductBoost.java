package io.mazy.souqly_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_boosts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBoost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "boost_type", nullable = false)
    private BoostType boostType;
    
    @Column(name = "boost_level", nullable = false)
    private Integer boostLevel = 1;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum BoostType {
        FEATURED,       // Mise en avant
        SPONSORED,      // Sponsorisé
        URGENT,         // Urgent
        PREMIUM,        // Premium
        TRENDING        // Tendances
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Méthode utilitaire pour vérifier si le boost est actif
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               startDate.isBefore(now) && 
               endDate.isAfter(now);
    }
} 