package io.mazy.souqly_backend.repository;

import io.mazy.souqly_backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    // Vérifier si un favori existe pour un utilisateur et un produit
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    // Trouver un favori spécifique
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);
    
    // Compter les favoris d'un produit
    long countByProductId(Long productId);
    
    // Récupérer tous les favoris d'un utilisateur
    List<Favorite> findByUserId(Long userId);
    
    // Récupérer tous les favoris d'un produit
    List<Favorite> findByProductId(Long productId);
    
    // Supprimer un favori spécifique
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    // Compter les favoris d'un utilisateur
    long countByUserId(Long userId);
} 