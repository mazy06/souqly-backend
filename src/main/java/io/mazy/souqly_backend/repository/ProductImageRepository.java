package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
} 