package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.ProductBoost;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.repository.ProductBoostRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoostService {
    
    private final ProductBoostRepository productBoostRepository;
    private final ProductRepository productRepository;
    
    /**
     * Récupère le boost d'un produit
     */
    public Optional<ProductBoost> getProductBoost(Long productId) {
        try {
            return productBoostRepository.findByProductIdAndIsActiveTrue(productId)
                .stream()
                .findFirst();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du boost pour le produit {}: {}", productId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Récupère tous les boosts actifs
     */
    public List<ProductBoost> getActiveBoosts() {
        try {
            return productBoostRepository.findByIsActiveTrue();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des boosts actifs: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Récupère les boosts d'un utilisateur
     */
    public List<ProductBoost> getBoostsByUserId(Long userId) {
        try {
            // Pour l'instant, retourner tous les boosts actifs
            // TODO: Ajouter un champ userId dans ProductBoost
            return productBoostRepository.findByIsActiveTrue();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des boosts de l'utilisateur {}: {}", userId, e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Crée un nouveau boost
     */
    public ProductBoost createBoost(ProductBoost boost) {
        try {
            return productBoostRepository.save(boost);
        } catch (Exception e) {
            log.error("Erreur lors de la création du boost: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Met à jour un boost
     */
    public ProductBoost updateBoost(ProductBoost boost) {
        try {
            return productBoostRepository.save(boost);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du boost: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Récupère un boost par ID
     */
    public Optional<ProductBoost> getBoostById(Long boostId) {
        try {
            return productBoostRepository.findById(boostId);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du boost {}: {}", boostId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Vérifie si un produit est boosté
     */
    public boolean isProductBoosted(Long productId) {
        try {
            return productBoostRepository.findByProductIdAndIsActiveTrue(productId).size() > 0;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du boost pour le produit {}: {}", productId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Récupère les produits boostés
     */
    public List<Product> getBoostedProducts() {
        try {
            List<ProductBoost> activeBoosts = productBoostRepository.findByIsActiveTrue();
            return activeBoosts.stream()
                .map(ProductBoost::getProduct)
                .filter(product -> product != null)
                .toList();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits boostés: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    /**
     * Nettoie les boosts expirés
     */
    public void cleanExpiredBoosts() {
        try {
            List<ProductBoost> expiredBoosts = productBoostRepository.findByEndDateBeforeAndIsActiveTrue(LocalDateTime.now());
            for (ProductBoost boost : expiredBoosts) {
                boost.setIsActive(false);
                productBoostRepository.save(boost);
            }
            log.info("{} boosts expirés nettoyés", expiredBoosts.size());
        } catch (Exception e) {
            log.error("Erreur lors du nettoyage des boosts expirés: {}", e.getMessage(), e);
        }
    }
} 