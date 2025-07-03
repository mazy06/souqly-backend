package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findBySellerId(Long sellerId);
    
    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = io.mazy.souqly_backend.entity.Product$ProductStatus.ACTIVE ORDER BY p.createdAt DESC")
    Page<Product> findActiveProducts(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    Page<Product> findProductsWithFilters(
        @Param("categoryId") Long categoryId,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("condition") String condition,
        @Param("brand") String brand,
        @Param("size") String size,
        @Param("search") String search,
        Pageable pageable
    );

    @Query("SELECT p FROM Product p")
    Page<Product> findAllProducts(Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") Long id);

    Page<Product> findByIsActiveTrue(Pageable pageable);

    // Utiliser la méthode Spring Data par défaut pour la pagination
    Page<Product> findAll(Pageable pageable);
} 