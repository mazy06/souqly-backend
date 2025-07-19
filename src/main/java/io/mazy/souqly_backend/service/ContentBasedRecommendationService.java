package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.UserProfile;
import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.ProductBoost;
import io.mazy.souqly_backend.repository.UserProfileRepository;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.ProductBoostRepository;
import io.mazy.souqly_backend.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentBasedRecommendationService {
    
    private final UserProfileRepository userProfileRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final ProductRepository productRepository;
    private final ProductBoostRepository productBoostRepository;
    private final UserService userService;
    
    /**
     * Calcule le profil utilisateur basé sur ses interactions
     */
    public UserProfile calculateUserProfile(Long userId) {
        try {
            // Récupérer toutes les interactions de l'utilisateur
            List<UserInteraction> interactions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Analyser les préférences
            Map<String, Integer> categoryPreferences = new HashMap<>();
            Map<String, Integer> brandPreferences = new HashMap<>();
            Map<String, Integer> conditionPreferences = new HashMap<>();
            List<BigDecimal> prices = new ArrayList<>();
            List<String> locations = new ArrayList<>();
            
            for (UserInteraction interaction : interactions) {
                if (interaction.getProduct() != null) {
                    Product product = interaction.getProduct();
                    
                    // Catégories
                    if (product.getCategory() != null) {
                        String categoryKey = product.getCategory().getKey();
                        categoryPreferences.merge(categoryKey, 1, Integer::sum);
                    }
                    
                    // Marques
                    if (product.getBrand() != null) {
                        brandPreferences.merge(product.getBrand(), 1, Integer::sum);
                    }
                    
                    // Conditions
                    if (product.getCondition() != null) {
                        conditionPreferences.merge(product.getCondition(), 1, Integer::sum);
                    }
                    
                    // Prix
                    if (product.getPrice() != null) {
                        prices.add(BigDecimal.valueOf(product.getPrice()));
                    }
                    
                    // Localisations
                    if (product.getCity() != null) {
                        locations.add(product.getCity());
                    }
                }
            }
            
            // Créer ou mettre à jour le profil utilisateur
            UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());
            
            // Convertir les préférences en JSON
            profile.setPreferredCategories(convertMapToJson(categoryPreferences));
            profile.setPreferredBrands(convertMapToJson(brandPreferences));
            profile.setPreferredConditions(convertMapToJson(conditionPreferences));
            profile.setPreferredLocations(convertListToJson(locations));
            
            // Calculer la fourchette de prix préférée
            if (!prices.isEmpty()) {
                BigDecimal minPrice = prices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = prices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                profile.setPreferredPriceRangeMin(minPrice);
                profile.setPreferredPriceRangeMax(maxPrice);
            }
            
            return userProfileRepository.save(profile);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul du profil utilisateur: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Génère des recommandations basées sur le contenu pour un utilisateur
     */
    public List<Product> getContentBasedRecommendations(Long userId, int limit) {
        try {
            // Calculer ou récupérer le profil utilisateur
            UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> calculateUserProfile(userId));
            
            if (profile == null) {
                return getFallbackRecommendations(limit);
            }
            
            // Récupérer tous les produits actifs
            List<Product> allProducts = productRepository.findByIsActiveTrue();
            
            // Calculer les scores de similarité
            List<Product> scoredProducts = allProducts.stream()
                .map(product -> new ProductScore(product, calculateSimilarityScore(product, profile)))
                .filter(score -> score.score > 0)
                .sorted(Comparator.comparing(ProductScore::getScore).reversed())
                .limit(limit)
                .map(ProductScore::getProduct)
                .collect(Collectors.toList());
            
            // Appliquer les boosts
            scoredProducts = applyBoosts(scoredProducts, userId);
            
            return scoredProducts;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations: {}", e.getMessage(), e);
            return getFallbackRecommendations(limit);
        }
    }
    
    /**
     * Calcule le score de similarité entre un produit et un profil utilisateur
     */
    private double calculateSimilarityScore(Product product, UserProfile profile) {
        double score = 0.0;
        
        // Score basé sur la catégorie (poids: 0.3)
        if (profile.getPreferredCategories() != null && product.getCategory() != null) {
            Map<String, Integer> categoryPrefs = parseJsonToMap(profile.getPreferredCategories());
            String categoryKey = product.getCategory().getKey();
            Integer categoryScore = categoryPrefs.get(categoryKey);
            if (categoryScore != null) {
                score += (categoryScore * 0.3);
            }
        }
        
        // Score basé sur la marque (poids: 0.2)
        if (profile.getPreferredBrands() != null && product.getBrand() != null) {
            Map<String, Integer> brandPrefs = parseJsonToMap(profile.getPreferredBrands());
            Integer brandScore = brandPrefs.get(product.getBrand());
            if (brandScore != null) {
                score += (brandScore * 0.2);
            }
        }
        
        // Score basé sur la condition (poids: 0.15)
        if (profile.getPreferredConditions() != null && product.getCondition() != null) {
            Map<String, Integer> conditionPrefs = parseJsonToMap(profile.getPreferredConditions());
            Integer conditionScore = conditionPrefs.get(product.getCondition());
            if (conditionScore != null) {
                score += (conditionScore * 0.15);
            }
        }
        
        // Score basé sur le prix (poids: 0.2)
        if (profile.getPreferredPriceRangeMin() != null && 
            profile.getPreferredPriceRangeMax() != null && 
            product.getPrice() != null) {
            
            BigDecimal productPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal minPrice = profile.getPreferredPriceRangeMin();
            BigDecimal maxPrice = profile.getPreferredPriceRangeMax();
            
            if (productPrice.compareTo(minPrice) >= 0 && productPrice.compareTo(maxPrice) <= 0) {
                score += 0.2;
            } else {
                // Pénalité pour les prix hors fourchette
                score += 0.05;
            }
        }
        
        // Score basé sur la localisation (poids: 0.15)
        if (profile.getPreferredLocations() != null && product.getCity() != null) {
            List<String> preferredLocations = parseJsonToList(profile.getPreferredLocations());
            if (preferredLocations.contains(product.getCity())) {
                score += 0.15;
            }
        }
        
        return score;
    }
    
    /**
     * Applique les boosts aux produits recommandés
     */
    private List<Product> applyBoosts(List<Product> products, Long userId) {
        try {
            // Récupérer les boosts actifs
            List<ProductBoost> activeBoosts = productBoostRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                LocalDateTime.now(), LocalDateTime.now());
            
            Map<Long, ProductBoost> boostMap = activeBoosts.stream()
                .collect(Collectors.toMap(boost -> boost.getProduct().getId(), boost -> boost));
            
            // Réorganiser les produits avec les boosts
            List<Product> boostedProducts = new ArrayList<>();
            List<Product> regularProducts = new ArrayList<>();
            
            for (Product product : products) {
                ProductBoost boost = boostMap.get(product.getId());
                if (boost != null && boost.isCurrentlyActive()) {
                    // Ajouter le produit boosté plusieurs fois selon le niveau
                    for (int i = 0; i < boost.getBoostLevel(); i++) {
                        boostedProducts.add(product);
                    }
                } else {
                    regularProducts.add(product);
                }
            }
            
            // Combiner les produits boostés et réguliers
            List<Product> result = new ArrayList<>();
            result.addAll(boostedProducts);
            result.addAll(regularProducts);
            
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'application des boosts: {}", e.getMessage(), e);
            return products;
        }
    }
    
    /**
     * Recommandations de fallback (produits populaires)
     */
    private List<Product> getFallbackRecommendations(int limit) {
        try {
            return productRepository.findByIsActiveTrueOrderByFavoriteCountDesc()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des recommandations de fallback: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    // Méthodes utilitaires pour la conversion JSON
    private String convertMapToJson(Map<String, Integer> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private String convertListToJson(List<String> list) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    private Map<String, Integer> parseJsonToMap(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    private List<String> parseJsonToList(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    // Classe interne pour les scores de produits
    private static class ProductScore {
        private final Product product;
        private final double score;
        
        public ProductScore(Product product, double score) {
            this.product = product;
            this.score = score;
        }
        
        public Product getProduct() { return product; }
        public double getScore() { return score; }
    }
} 