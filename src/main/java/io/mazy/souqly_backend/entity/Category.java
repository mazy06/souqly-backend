package io.mazy.souqly_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "category_key", nullable = false, unique = true)
    private String key;
    
    @Column(nullable = false)
    private String label;
    
    @Column(name = "icon_name")
    private String iconName;
    
    @Column(name = "badge_text")
    private String badgeText;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Category> children = new ArrayList<>();
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "is_active")
    private boolean active = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isRoot() {
        return parent == null;
    }
    
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    public void addChild(Category child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Category child) {
        if (children != null) {
            children.remove(child);
            child.setParent(null);
        }
    }
} 