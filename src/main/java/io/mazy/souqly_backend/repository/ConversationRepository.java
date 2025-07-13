package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    
    // Trouver toutes les conversations d'un utilisateur (acheteur ou vendeur)
    @Query("SELECT c FROM ConversationEntity c WHERE c.buyer.id = :userId OR c.seller.id = :userId ORDER BY c.lastMessageTime DESC")
    List<ConversationEntity> findByUserId(@Param("userId") Long userId);
    
    // Trouver une conversation par son ID unique
    Optional<ConversationEntity> findByConversationId(String conversationId);
    
    // Trouver une conversation entre un acheteur et un vendeur pour un produit spécifique
    @Query("SELECT c FROM ConversationEntity c WHERE c.buyer.id = :buyerId AND c.seller.id = :sellerId AND c.product.id = :productId")
    Optional<ConversationEntity> findByBuyerAndSellerAndProduct(
        @Param("buyerId") Long buyerId, 
        @Param("sellerId") Long sellerId, 
        @Param("productId") Long productId
    );
    
    // Vérifier si une conversation existe entre deux utilisateurs pour un produit
    @Query("SELECT COUNT(c) > 0 FROM ConversationEntity c WHERE c.buyer.id = :buyerId AND c.seller.id = :sellerId AND c.product.id = :productId")
    boolean existsByBuyerAndSellerAndProduct(
        @Param("buyerId") Long buyerId, 
        @Param("sellerId") Long sellerId, 
        @Param("productId") Long productId
    );
} 