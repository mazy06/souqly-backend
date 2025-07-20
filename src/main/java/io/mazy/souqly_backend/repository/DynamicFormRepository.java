package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.DynamicForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicFormRepository extends JpaRepository<DynamicForm, Long> {
    
    @Query("SELECT df FROM DynamicForm df WHERE df.category.id = :categoryId AND df.isActive = true")
    Optional<DynamicForm> findByCategoryIdAndActive(@Param("categoryId") Long categoryId);
    
    @Query("SELECT df FROM DynamicForm df WHERE df.category.id = :categoryId")
    List<DynamicForm> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT df FROM DynamicForm df WHERE df.isActive = true")
    List<DynamicForm> findAllActive();
    
    @Query("SELECT df FROM DynamicForm df ORDER BY df.id")
    List<DynamicForm> findAllForms();
} 