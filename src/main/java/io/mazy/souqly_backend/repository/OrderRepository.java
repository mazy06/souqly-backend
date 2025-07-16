package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Récupère toutes les commandes d'un acheteur
     */
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    /**
     * Récupère toutes les commandes d'un vendeur
     */
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    /**
     * Récupère une commande par son numéro
     */
    Order findByOrderNumber(String orderNumber);
    
    /**
     * Récupère les commandes par statut pour un acheteur
     */
    List<Order> findByBuyerIdAndStatusOrderByCreatedAtDesc(Long buyerId, Order.OrderStatus status);
    
    /**
     * Compte le nombre de commandes par statut pour un acheteur
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.buyer.id = :buyerId AND o.status = :status")
    long countByBuyerIdAndStatus(@Param("buyerId") Long buyerId, @Param("status") Order.OrderStatus status);
    
    /**
     * Vérifie si un numéro de commande existe déjà
     */
    boolean existsByOrderNumber(String orderNumber);
} 