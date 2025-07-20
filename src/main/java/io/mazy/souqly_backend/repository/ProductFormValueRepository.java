package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ProductFormValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductFormValueRepository extends JpaRepository<ProductFormValue, Long> {
    
    @Query("SELECT pfv FROM ProductFormValue pfv WHERE pfv.product.id = :productId")
    List<ProductFormValue> findByProductId(@Param("productId") Long productId);
    
    @Query("SELECT pfv FROM ProductFormValue pfv WHERE pfv.product.id = :productId AND pfv.field.id = :fieldId")
    Optional<ProductFormValue> findByProductIdAndFieldId(@Param("productId") Long productId, @Param("fieldId") Long fieldId);
    
    @Query("SELECT pfv FROM ProductFormValue pfv WHERE pfv.field.id = :fieldId")
    List<ProductFormValue> findByFieldId(@Param("fieldId") Long fieldId);
    
    @Query("DELETE FROM ProductFormValue pfv WHERE pfv.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
} 