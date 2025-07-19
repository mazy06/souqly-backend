package io.mazy.souqly_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "preferred_categories", columnDefinition = "TEXT")
    private String preferredCategories; // JSON array de catégories
    
    @Column(name = "preferred_price_range_min", precision = 10, scale = 2)
    private BigDecimal preferredPriceRangeMin;
    
    @Column(name = "preferred_price_range_max", precision = 10, scale = 2)
    private BigDecimal preferredPriceRangeMax;
    
    @Column(name = "preferred_brands", columnDefinition = "TEXT")
    private String preferredBrands; // JSON array de marques
    
    @Column(name = "preferred_conditions", columnDefinition = "TEXT")
    private String preferredConditions; // JSON array de conditions
    
    @Column(name = "preferred_locations", columnDefinition = "TEXT")
    private String preferredLocations; // JSON array de localisations
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
 