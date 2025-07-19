package io.mazy.souqly_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_similarities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimilarity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id_1", nullable = false)
    private Long userId1;
    
    @Column(name = "user_id_2", nullable = false)
    private Long userId2;
    
    @Column(name = "similarity_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal similarityScore;
    
    @Column(name = "last_calculated", nullable = false)
    private LocalDateTime lastCalculated = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        lastCalculated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastCalculated = LocalDateTime.now();
    }
} 