package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.CategoryDto;
import io.mazy.souqly_backend.dto.CategoryReorderRequest;
import io.mazy.souqly_backend.entity.Category;
import io.mazy.souqly_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getRootCategories() {
        return categoryRepository.findActiveRootCategories().stream()
                .map(category -> new CategoryDto(category, true))
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getChildrenCategories(Long parentId) {
        Optional<Category> parent = categoryRepository.findById(parentId);
        if (parent.isPresent()) {
            return categoryRepository.findActiveChildren(parent.get()).stream()
                    .map(category -> new CategoryDto(category, true))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
    
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryDto::new);
    }
    
    public Optional<CategoryDto> getCategoryByKey(String key) {
        return categoryRepository.findByKey(key)
                .map(CategoryDto::new);
    }
    
    public CategoryDto createCategory(Category category) {
        // Set parent if parentId is provided
        if (category.getParent() != null && category.getParent().getId() != null) {
            Optional<Category> parent = categoryRepository.findById(category.getParent().getId());
            parent.ifPresent(category::setParent);
        }
        
        Category savedCategory = categoryRepository.save(category);
        return new CategoryDto(savedCategory);
    }
    
    public Optional<CategoryDto> updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setKey(categoryDetails.getKey());
                    category.setLabel(categoryDetails.getLabel());
                    category.setIconName(categoryDetails.getIconName());
                    category.setBadgeText(categoryDetails.getBadgeText());
                    category.setSortOrder(categoryDetails.getSortOrder());
                    category.setActive(categoryDetails.isActive());
                    
                    // Update parent if provided
                    if (categoryDetails.getParent() != null && categoryDetails.getParent().getId() != null) {
                        Optional<Category> parent = categoryRepository.findById(categoryDetails.getParent().getId());
                        parent.ifPresent(category::setParent);
                    } else {
                        category.setParent(null);
                    }
                    
                    Category savedCategory = categoryRepository.save(category);
                    return new CategoryDto(savedCategory);
                });
    }
    
    public boolean deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            // Check if category has children
            if (!category.get().getChildren().isEmpty()) {
                throw new IllegalStateException("Cannot delete category with children. Delete children first.");
            }
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<CategoryDto> searchCategories(String searchTerm) {
        return categoryRepository.searchCategories(searchTerm).stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }
    
    public boolean existsByKey(String key) {
        return categoryRepository.existsByKey(key);
    }
    
    public List<CategoryDto> getCategoryTree() {
        return categoryRepository.findActiveRootCategories().stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getAllCategoryTree() {
        return categoryRepository.findAllRootCategories().stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }
    
    public CategoryDto moveCategory(Long categoryId, Long newParentId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Category not found");
        }
        
        Category category = categoryOpt.get();
        
        // Vérifier que le nouveau parent n'est pas la catégorie elle-même
        if (newParentId != null && newParentId.equals(categoryId)) {
            throw new IllegalArgumentException("A category cannot be its own parent");
        }
        
        // Vérifier que le nouveau parent n'est pas un descendant de la catégorie
        if (newParentId != null && isDescendant(category, newParentId)) {
            throw new IllegalArgumentException("Cannot move category to its own descendant");
        }
        
        if (newParentId == null) {
            category.setParent(null);
        } else {
            Optional<Category> newParentOpt = categoryRepository.findById(newParentId);
            if (newParentOpt.isEmpty()) {
                throw new IllegalArgumentException("New parent category not found");
            }
            category.setParent(newParentOpt.get());
        }
        
        Category savedCategory = categoryRepository.save(category);
        return new CategoryDto(savedCategory);
    }
    
    private boolean isDescendant(Category category, Long potentialDescendantId) {
        if (category.getChildren() == null || category.getChildren().isEmpty()) {
            return false;
        }
        
        for (Category child : category.getChildren()) {
            if (child.getId().equals(potentialDescendantId)) {
                return true;
            }
            if (isDescendant(child, potentialDescendantId)) {
                return true;
            }
        }
        return false;
    }
    
    public List<CategoryDto> reorderCategories(List<CategoryReorderRequest> reorderRequests) {
        List<CategoryDto> updatedCategories = new ArrayList<>();
        
        for (CategoryReorderRequest request : reorderRequests) {
            Optional<Category> categoryOpt = categoryRepository.findById(request.getId());
            if (categoryOpt.isEmpty()) {
                throw new IllegalArgumentException("Category not found with id: " + request.getId());
            }
            
            Category category = categoryOpt.get();
            category.setSortOrder(request.getSortOrder());
            
            // Mettre à jour le parent si nécessaire
            if (request.getParentId() != null) {
                Optional<Category> newParentOpt = categoryRepository.findById(request.getParentId());
                if (newParentOpt.isEmpty()) {
                    throw new IllegalArgumentException("Parent category not found with id: " + request.getParentId());
                }
                category.setParent(newParentOpt.get());
            } else {
                category.setParent(null);
            }
            
            Category savedCategory = categoryRepository.save(category);
            updatedCategories.add(new CategoryDto(savedCategory));
        }
        
        return updatedCategories;
    }
} 