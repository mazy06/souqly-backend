package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    
    private Long id;
    private String key;
    private String label;
    private String iconName;
    private String badgeText;
    private Long parentId;
    private List<CategoryDto> children;
    private Integer sortOrder;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor from entity
    public CategoryDto(Category category) {
        this.id = category.getId();
        this.key = category.getKey();
        this.label = category.getLabel();
        this.iconName = category.getIconName();
        this.badgeText = category.getBadgeText();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;
        this.sortOrder = category.getSortOrder();
        this.active = category.isActive();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
        
        // Convert children recursively
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            this.children = category.getChildren().stream()
                    .map(CategoryDto::new)
                    .collect(Collectors.toList());
        }
    }
    
    // Constructor without children (for performance)
    public CategoryDto(Category category, boolean includeChildren) {
        this.id = category.getId();
        this.key = category.getKey();
        this.label = category.getLabel();
        this.iconName = category.getIconName();
        this.badgeText = category.getBadgeText();
        this.parentId = category.getParent() != null ? category.getParent().getId() : null;
        this.sortOrder = category.getSortOrder();
        this.active = category.isActive();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
        
        if (includeChildren && category.getChildren() != null && !category.getChildren().isEmpty()) {
            this.children = category.getChildren().stream()
                    .map(child -> new CategoryDto(child, false))
                    .collect(Collectors.toList());
        }
    }
} 