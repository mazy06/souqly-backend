package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.FavoriteResponse;
import io.mazy.souqly_backend.entity.Favorite;
import io.mazy.souqly_backend.repository.FavoriteRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Ajouter ou retirer un produit des favoris
     */
    @Transactional
    public FavoriteResponse toggleFavorite(Long productId, Long userId) {
        // Vérifier si le favori existe déjà
        Optional<Favorite> existingFavorite = favoriteRepository
            .findByUserIdAndProductId(userId, productId);
        
        if (existingFavorite.isPresent()) {
            // Retirer des favoris
            favoriteRepository.delete(existingFavorite.get());
            productRepository.decrementFavoriteCount(productId);
            return new FavoriteResponse(false, getFavoriteCount(productId));
        } else {
            // Ajouter aux favoris
            Favorite favorite = new Favorite(userId, productId);
            favoriteRepository.save(favorite);
            productRepository.incrementFavoriteCount(productId);
            return new FavoriteResponse(true, getFavoriteCount(productId));
        }
    }
    
    /**
     * Vérifier si un produit est en favori pour un utilisateur
     */
    public boolean isFavorite(Long productId, Long userId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
    
    /**
     * Récupérer le nombre de favoris d'un produit
     */
    public int getFavoriteCount(Long productId) {
        return (int) favoriteRepository.countByProductId(productId);
    }
    
    /**
     * Récupérer tous les favoris d'un utilisateur
     */
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }
    
    /**
     * Récupérer tous les favoris d'un produit
     */
    public List<Favorite> getProductFavorites(Long productId) {
        return favoriteRepository.findByProductId(productId);
    }
    
    /**
     * Supprimer un favori spécifique
     */
    public void removeFavorite(Long productId, Long userId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    /**
     * Compter les favoris d'un utilisateur
     */
    public int getUserFavoriteCount(Long userId) {
        return (int) favoriteRepository.countByUserId(userId);
    }

    public Map<Long, Long> getFavoriteCountsForProducts(List<Long> productIds) {
        List<FavoriteRepository.ProductFavoriteCount> counts = favoriteRepository.countFavoritesByProductIds(productIds);
        return counts.stream().collect(Collectors.toMap(FavoriteRepository.ProductFavoriteCount::getProductId, FavoriteRepository.ProductFavoriteCount::getCount));
    }
} 