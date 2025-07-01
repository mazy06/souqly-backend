package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByKey(String key);
    
    List<Category> findByParentIsNullOrderBySortOrderAsc();
    
    List<Category> findByParentOrderBySortOrderAsc(Category parent);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true ORDER BY c.sortOrder")
    List<Category> findActiveRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.sortOrder")
    List<Category> findAllRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent = :parent AND c.active = true ORDER BY c.sortOrder")
    List<Category> findActiveChildren(@Param("parent") Category parent);
    
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.sortOrder")
    List<Category> findAllActive();
    
    boolean existsByKey(String key);
    
    @Query("SELECT c FROM Category c WHERE c.key LIKE %:searchTerm% OR c.label LIKE %:searchTerm%")
    List<Category> searchCategories(@Param("searchTerm") String searchTerm);
} 