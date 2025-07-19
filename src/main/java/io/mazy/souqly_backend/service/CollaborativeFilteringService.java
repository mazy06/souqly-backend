package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.UserSimilarity;
import io.mazy.souqly_backend.entity.ProductBoost;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.UserSimilarityRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.ProductBoostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollaborativeFilteringService {
    
    private final UserInteractionRepository userInteractionRepository;
    private final UserSimilarityRepository userSimilarityRepository;
    private final ProductRepository productRepository;
    private final ProductBoostRepository productBoostRepository;
    
    /**
     * Calcule la similarité entre deux utilisateurs basée sur leurs interactions
     */
    public double calculateUserSimilarity(Long userId1, Long userId2) {
        try {
            // Récupérer les interactions des deux utilisateurs
            List<UserInteraction> user1Interactions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId1);
            List<UserInteraction> user2Interactions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId2);
            
            // Créer des ensembles de produits avec lesquels chaque utilisateur a interagi
            Set<Long> user1Products = user1Interactions.stream()
                .filter(interaction -> interaction.getProduct() != null)
                .map(interaction -> interaction.getProduct().getId())
                .collect(Collectors.toSet());
            
            Set<Long> user2Products = user2Interactions.stream()
                .filter(interaction -> interaction.getProduct() != null)
                .map(interaction -> interaction.getProduct().getId())
                .collect(Collectors.toSet());
            
            // Calculer l'intersection et l'union
            Set<Long> intersection = new HashSet<>(user1Products);
            intersection.retainAll(user2Products);
            
            Set<Long> union = new HashSet<>(user1Products);
            union.addAll(user2Products);
            
            // Calculer le coefficient de Jaccard
            if (union.isEmpty()) {
                return 0.0;
            }
            
            return (double) intersection.size() / union.size();
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de similarité entre utilisateurs: {}", e.getMessage(), e);
            return 0.0;
        }
    }
    
    /**
     * Calcule les similarités pour un utilisateur et les sauvegarde
     */
    public void calculateAndSaveUserSimilarities(Long userId) {
        try {
            // Récupérer tous les autres utilisateurs qui ont des interactions
            List<Long> allUserIds = userInteractionRepository.findDistinctUserIds();
            
            for (Long otherUserId : allUserIds) {
                if (!otherUserId.equals(userId)) {
                    double similarity = calculateUserSimilarity(userId, otherUserId);
                    
                    if (similarity > 0.1) { // Seuil minimum de similarité
                        // Sauvegarder ou mettre à jour la similarité
                        UserSimilarity userSimilarity = userSimilarityRepository
                            .findByUserId1AndUserId2(userId, otherUserId)
                            .orElseGet(() -> {
                                UserSimilarity newSimilarity = new UserSimilarity();
                                newSimilarity.setUserId1(userId);
                                newSimilarity.setUserId2(otherUserId);
                                return newSimilarity;
                            });
                        
                        userSimilarity.setSimilarityScore(BigDecimal.valueOf(similarity));
                        userSimilarity.setLastCalculated(LocalDateTime.now());
                        userSimilarityRepository.save(userSimilarity);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des similarités utilisateur: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Génère des recommandations basées sur la collaboration
     */
    public List<Product> getCollaborativeRecommendations(Long userId, int limit) {
        try {
            // Récupérer les utilisateurs similaires
            List<UserSimilarity> similarities = userSimilarityRepository
                .findByUserId1OrderBySimilarityScoreDesc(userId);
            
            if (similarities.isEmpty()) {
                return getFallbackRecommendations(limit);
            }
            
            // Récupérer les produits avec lesquels les utilisateurs similaires ont interagi
            Set<Long> recommendedProductIds = new HashSet<>();
            Set<Long> userInteractedProducts = userInteractionRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(interaction -> interaction.getProduct() != null)
                .map(interaction -> interaction.getProduct().getId())
                .collect(Collectors.toSet());
            
            for (UserSimilarity similarity : similarities) {
                if (similarity.getSimilarityScore().compareTo(BigDecimal.valueOf(0.3)) > 0) { // Seuil de similarité
                    Long similarUserId = similarity.getUserId2();
                    
                    // Récupérer les produits avec lesquels l'utilisateur similaire a interagi
                    List<UserInteraction> similarUserInteractions = userInteractionRepository
                        .findByUserIdOrderByCreatedAtDesc(similarUserId);
                    
                    for (UserInteraction interaction : similarUserInteractions) {
                        if (interaction.getProduct() != null && 
                            !userInteractedProducts.contains(interaction.getProduct().getId())) {
                            recommendedProductIds.add(interaction.getProduct().getId());
                        }
                    }
                }
            }
            
            // Récupérer les produits recommandés
            List<Product> recommendedProducts = productRepository
                .findByIdInAndIsActiveTrue(new ArrayList<>(recommendedProductIds));
            
            // Trier par popularité et appliquer les boosts
            recommendedProducts.sort(Comparator.comparing(Product::getFavoriteCount).reversed());
            recommendedProducts = applyBoosts(recommendedProducts, userId);
            
            return recommendedProducts.stream().limit(limit).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations collaboratives: {}", e.getMessage(), e);
            return getFallbackRecommendations(limit);
        }
    }
    
    /**
     * Génère des recommandations hybrides (content-based + collaborative)
     */
    public List<Product> getHybridRecommendations(Long userId, int limit) {
        try {
            // Récupérer les recommandations content-based
            ContentBasedRecommendationService contentBasedService = new ContentBasedRecommendationService(
                null, null, productRepository, productBoostRepository, null);
            List<Product> contentBasedRecs = contentBasedService.getContentBasedRecommendations(userId, limit);
            
            // Récupérer les recommandations collaboratives
            List<Product> collaborativeRecs = getCollaborativeRecommendations(userId, limit);
            
            // Combiner les deux listes avec des poids
            Map<Long, Double> productScores = new HashMap<>();
            
            // Ajouter les scores content-based (poids: 0.6)
            for (int i = 0; i < contentBasedRecs.size(); i++) {
                Product product = contentBasedRecs.get(i);
                double score = (limit - i) * 0.6 / limit;
                productScores.merge(product.getId(), score, Double::sum);
            }
            
            // Ajouter les scores collaboratifs (poids: 0.4)
            for (int i = 0; i < collaborativeRecs.size(); i++) {
                Product product = collaborativeRecs.get(i);
                double score = (limit - i) * 0.4 / limit;
                productScores.merge(product.getId(), score, Double::sum);
            }
            
            // Trier par score total
            List<Product> allProducts = new ArrayList<>();
            allProducts.addAll(contentBasedRecs);
            allProducts.addAll(collaborativeRecs);
            
            allProducts = allProducts.stream()
                .distinct()
                .sorted((p1, p2) -> Double.compare(
                    productScores.getOrDefault(p2.getId(), 0.0),
                    productScores.getOrDefault(p1.getId(), 0.0)
                ))
                .limit(limit)
                .collect(Collectors.toList());
            
            // Appliquer les boosts
            return applyBoosts(allProducts, userId);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations hybrides: {}", e.getMessage(), e);
            return getFallbackRecommendations(limit);
        }
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
} 