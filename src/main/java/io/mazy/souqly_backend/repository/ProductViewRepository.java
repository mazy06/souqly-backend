package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    
    // Vérifier si un utilisateur a déjà vu un produit
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    
    // Compter les vues uniques d'un produit
    long countByProductId(Long productId);
    
    // Récupérer toutes les vues d'un produit
    List<ProductView> findByProductId(Long productId);
    
    // Récupérer toutes les vues d'un utilisateur
    List<ProductView> findByUserId(Long userId);
    
    // Récupérer les vues récentes d'un produit (dernières 24h)
    @Query("SELECT pv FROM ProductView pv WHERE pv.product.id = :productId AND pv.viewedAt >= :since")
    List<ProductView> findRecentViewsByProductId(@Param("productId") Long productId, @Param("since") LocalDateTime since);
    
    // Récupérer une vue spécifique
    Optional<ProductView> findByProductIdAndUserId(Long productId, Long userId);
    
    // Supprimer les anciennes vues (nettoyage)
    void deleteByViewedAtBefore(LocalDateTime date);
} 