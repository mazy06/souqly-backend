package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ProductBoost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductBoostRepository extends JpaRepository<ProductBoost, Long> {
    
    /**
     * Trouve tous les boosts actifs pour un produit
     */
    List<ProductBoost> findByProductIdAndIsActiveTrue(Long productId);
    
    /**
     * Trouve tous les boosts actifs
     */
    List<ProductBoost> findByIsActiveTrue();
    
    /**
     * Trouve les boosts actifs dans une période donnée
     */
    @Query("SELECT pb FROM ProductBoost pb WHERE pb.isActive = true AND pb.startDate <= :now AND pb.endDate >= :now")
    List<ProductBoost> findActiveBoosts(@Param("now") LocalDateTime now);
    
    /**
     * Trouve les boosts actifs pour une période spécifique
     */
    List<ProductBoost> findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
        LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Trouve les boosts par type
     */
    List<ProductBoost> findByBoostTypeAndIsActiveTrue(ProductBoost.BoostType boostType);
    
    /**
     * Trouve les boosts expirés
     */
    List<ProductBoost> findByEndDateBeforeAndIsActiveTrue(LocalDateTime date);
    
    /**
     * Compte les boosts actifs pour une période spécifique
     */
    @Query("SELECT COUNT(pb) FROM ProductBoost pb WHERE pb.isActive = true AND pb.startDate <= :endDate AND pb.endDate >= :startDate")
    long countByIsActiveTrueAndStartDateBeforeAndEndDateAfter(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 