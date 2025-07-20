package io.mazy.souqly_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "form_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    @JsonIgnore
    private DynamicForm form;
    
    @Column(name = "field_key", nullable = false)
    private String fieldKey;
    
    @Column(name = "field_label", nullable = false)
    private String fieldLabel;
    
    @Column(name = "field_type", nullable = false)
    private String fieldType;
    
    @Column(name = "field_placeholder")
    private String fieldPlaceholder;
    
    @Column(name = "field_required", nullable = false)
    private Boolean fieldRequired = false;
    
    @Column(name = "field_options")
    private String fieldOptions;
    
    @Column(name = "field_validation")
    private String fieldValidation;
    
    @Column(name = "field_order", nullable = false)
    private Integer fieldOrder = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
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
} 