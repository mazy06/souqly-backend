package io.mazy.souqly_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "viewed_at")
    private LocalDateTime viewedAt = LocalDateTime.now();
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    public ProductView(Product product, User user) {
        this.product = product;
        this.user = user;
    }
    
    public ProductView(Product product, User user, String ipAddress, String userAgent) {
        this.product = product;
        this.user = user;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
} 