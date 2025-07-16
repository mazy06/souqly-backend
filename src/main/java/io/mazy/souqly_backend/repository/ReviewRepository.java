package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Trouve tous les avis d'un vendeur
     */
    List<Review> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    /**
     * Trouve tous les avis d'un vendeur (sans tri)
     */
    List<Review> findBySellerId(Long sellerId);
    
    /**
     * Trouve tous les avis d'un produit
     */
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    /**
     * Trouve tous les avis d'un acheteur
     */
    List<Review> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    /**
     * Vérifie si un utilisateur a déjà laissé un avis pour un produit
     */
    boolean existsByProductIdAndBuyerId(Long productId, Long buyerId);
    
    /**
     * Trouve un avis par produit et acheteur
     */
    Optional<Review> findByProductIdAndBuyerId(Long productId, Long buyerId);
    
    /**
     * Compte le nombre d'avis d'un vendeur
     */
    long countBySellerId(Long sellerId);
    
    /**
     * Calcule la note moyenne d'un vendeur
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.sellerId = :sellerId")
    Double getAverageRatingBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * Trouve la distribution des notes pour un vendeur
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.sellerId = :sellerId GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * Trouve les avis avec les informations du produit et du vendeur
     */
    @Query("SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.product p " +
           "LEFT JOIN FETCH r.seller s " +
           "LEFT JOIN FETCH r.buyer b " +
           "WHERE r.sellerId = :sellerId " +
           "ORDER BY r.createdAt DESC")
    List<Review> findBySellerIdWithDetails(@Param("sellerId") Long sellerId);
} 