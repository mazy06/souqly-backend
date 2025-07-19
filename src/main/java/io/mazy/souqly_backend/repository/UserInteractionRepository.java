package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {
    
    /**
     * Trouve toutes les interactions d'un utilisateur, triées par date de création décroissante
     */
    List<UserInteraction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Trouve toutes les interactions pour un produit, triées par date de création décroissante
     */
    List<UserInteraction> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    /**
     * Trouve les interactions récentes d'un utilisateur avec limite
     */
    @Query("SELECT ui FROM UserInteraction ui WHERE ui.user.id = :userId ORDER BY ui.createdAt DESC")
    List<UserInteraction> findByUserIdOrderByCreatedAtDescLimit(@Param("userId") Long userId, int limit);
    
    /**
     * Trouve les interactions par type pour un utilisateur
     */
    List<UserInteraction> findByUserIdAndInteractionTypeOrderByCreatedAtDesc(Long userId, UserInteraction.InteractionType type);
    
    /**
     * Trouve les interactions par type pour un produit
     */
    List<UserInteraction> findByProductIdAndInteractionTypeOrderByCreatedAtDesc(Long productId, UserInteraction.InteractionType type);
    
    /**
     * Compte le nombre d'interactions par type pour un utilisateur
     */
    long countByUserIdAndInteractionType(Long userId, UserInteraction.InteractionType type);
    
    /**
     * Compte le nombre d'interactions par type pour un produit
     */
    long countByProductIdAndInteractionType(Long productId, UserInteraction.InteractionType type);
    
    /**
     * Trouve les produits les plus vus par un utilisateur
     */
    @Query("SELECT ui.product.id, COUNT(ui) as viewCount FROM UserInteraction ui " +
           "WHERE ui.user.id = :userId AND ui.interactionType = 'VIEW' " +
           "GROUP BY ui.product.id ORDER BY viewCount DESC")
    List<Object[]> findMostViewedProductsByUser(@Param("userId") Long userId);
    
    /**
     * Trouve les utilisateurs qui ont interagi avec un produit
     */
    @Query("SELECT DISTINCT ui.user.id FROM UserInteraction ui WHERE ui.product.id = :productId")
    List<Long> findUsersWhoInteractedWithProduct(@Param("productId") Long productId);
    
    /**
     * Trouve tous les IDs d'utilisateurs distincts qui ont des interactions
     */
    @Query("SELECT DISTINCT ui.user.id FROM UserInteraction ui")
    List<Long> findDistinctUserIds();
    
    // Méthodes pour les analytics
    
    /**
     * Compte les interactions entre deux dates
     */
    @Query("SELECT COUNT(ui) FROM UserInteraction ui WHERE ui.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Compte les interactions par type entre deux dates
     */
    @Query("SELECT COUNT(ui) FROM UserInteraction ui WHERE ui.interactionType = :type AND ui.createdAt BETWEEN :startDate AND :endDate")
    long countByInteractionTypeAndCreatedAtBetween(@Param("type") UserInteraction.InteractionType type, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    /**
     * Compte les utilisateurs distincts entre deux dates
     */
    @Query("SELECT COUNT(DISTINCT ui.user.id) FROM UserInteraction ui WHERE ui.createdAt BETWEEN :startDate AND :endDate")
    long countDistinctUserIdByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Trouve les interactions entre deux dates
     */
    @Query("SELECT ui FROM UserInteraction ui WHERE ui.createdAt BETWEEN :startDate AND :endDate ORDER BY ui.createdAt")
    List<UserInteraction> findByCreatedAtBetweenOrderByCreatedAt(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Compte les interactions pour un produit par type entre deux dates
     */
    @Query("SELECT COUNT(ui) FROM UserInteraction ui WHERE ui.product.id = :productId AND ui.interactionType = :type AND ui.createdAt BETWEEN :startDate AND :endDate")
    long countByProductIdAndInteractionTypeAndCreatedAtBetween(@Param("productId") Long productId,
                                                             @Param("type") UserInteraction.InteractionType type,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
} 