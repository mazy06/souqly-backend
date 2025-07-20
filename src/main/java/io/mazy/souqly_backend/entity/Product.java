package io.mazy.souqly_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Cacheable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 120)
    private String title;
    
    @Column(nullable = false, length = 4000)
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ProductImage> images = new ArrayList<>();
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id")
    private User seller;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private String brand;
    
    @Column
    private String size;
    
    @Column(nullable = false)
    private String condition;
    
    @Column(name = "price_with_fees", precision = 10, scale = 2)
    private BigDecimal priceWithFees;
    
    @Column(name = "shipping_info")
    private String shippingInfo;
    
    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "product_measurements", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "measurement_key")
    @Column(name = "measurement_value")
    private java.util.Map<String, String> measurements = new java.util.HashMap<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.ACTIVE;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    
    
    
    @Column(name = "city")
    private String city;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "is_boosted")
    private Boolean isBoosted = false;
    
    @Column(name = "boost_level")
    private Integer boostLevel = 0;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ProductStatus {
        ACTIVE, SOLD, INACTIVE, DELETED
    }
} 