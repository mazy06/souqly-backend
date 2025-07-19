package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.ProductBoost;
import io.mazy.souqly_backend.entity.UserProfile;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.ProductBoostRepository;
import io.mazy.souqly_backend.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimizationService {
    
    private final UserInteractionRepository userInteractionRepository;
    private final ProductBoostRepository productBoostRepository;
    private final UserProfileRepository userProfileRepository;
    
    // Cache pour les recommandations
    private final Map<String, Object> recommendationCache = new ConcurrentHashMap<>();
    private final Map<String, Object> boostCache = new ConcurrentHashMap<>();
    
    /**
     * Optimise les recommandations en utilisant le cache
     */
    @Cacheable(value = "recommendations", key = "#userId + '_' + #limit")
    public List<Map<String, Object>> getOptimizedRecommendations(Long userId, int limit) {
        try {
            // Simulation d'optimisation des recommandations
            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            // Ajouter des recommandations simulées avec scores optimisés
            for (int i = 0; i < limit; i++) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("productId", 1000 + i);
                recommendation.put("score", 0.8 + (Math.random() * 0.2));
                recommendation.put("algorithm", getOptimalAlgorithm(userId));
                recommendation.put("boosted", Math.random() > 0.7);
                recommendations.add(recommendation);
            }
            
            // Trier par score
            recommendations.sort((a, b) -> Double.compare(
                (Double) b.get("score"), 
                (Double) a.get("score")
            ));
            
            return recommendations;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation des recommandations: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Optimise les boosts en fonction des performances
     */
    public List<ProductBoost> getOptimizedBoosts() {
        try {
            List<ProductBoost> allBoosts = productBoostRepository.findByIsActiveTrue();
            
            // Filtrer et optimiser les boosts selon les performances
            return allBoosts.stream()
                .filter(boost -> isBoostPerformingWell(boost))
                .sorted(Comparator.comparing(ProductBoost::getBoostLevel).reversed())
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation des boosts: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Optimise les profils utilisateur
     */
    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void optimizeUserProfiles() {
        try {
            log.info("Début de l'optimisation des profils utilisateur...");
            
            List<UserProfile> profiles = userProfileRepository.findAll();
            
            for (UserProfile profile : profiles) {
                optimizeUserProfile(profile);
            }
            
            log.info("Optimisation des profils utilisateur terminée");
            
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation des profils: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Nettoie le cache périodiquement
     */
    @Scheduled(fixedRate = 1800000) // Toutes les 30 minutes
    public void cleanCache() {
        try {
            log.info("Nettoyage du cache...");
            
            // Supprimer les entrées expirées du cache
            recommendationCache.entrySet().removeIf(entry -> {
                Object value = entry.getValue();
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    LocalDateTime timestamp = (LocalDateTime) map.get("timestamp");
                    return timestamp != null && timestamp.isBefore(LocalDateTime.now().minusMinutes(30));
                }
                return false;
            });
            
            boostCache.clear();
            
            log.info("Cache nettoyé");
            
        } catch (Exception e) {
            log.error("Erreur lors du nettoyage du cache: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Optimise les algorithmes de recommandation
     */
    public Map<String, Object> optimizeAlgorithms(Long userId) {
        try {
            Map<String, Object> optimization = new HashMap<>();
            
            // Analyser les performances des algorithmes pour cet utilisateur
            double contentBasedScore = analyzeContentBasedPerformance(userId);
            double collaborativeScore = analyzeCollaborativePerformance(userId);
            double hybridScore = analyzeHybridPerformance(userId);
            
            // Déterminer l'algorithme optimal
            String optimalAlgorithm = determineOptimalAlgorithm(contentBasedScore, collaborativeScore, hybridScore);
            
            optimization.put("contentBasedScore", contentBasedScore);
            optimization.put("collaborativeScore", collaborativeScore);
            optimization.put("hybridScore", hybridScore);
            optimization.put("optimalAlgorithm", optimalAlgorithm);
            optimization.put("recommendation", Map.of(
                "useContentBased", contentBasedScore > 0.7,
                "useCollaborative", collaborativeScore > 0.7,
                "useHybrid", hybridScore > 0.8,
                "weights", Map.of(
                    "contentBased", contentBasedScore,
                    "collaborative", collaborativeScore,
                    "hybrid", hybridScore
                )
            ));
            
            return optimization;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation des algorithmes: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Optimise les paramètres de boost
     */
    public Map<String, Object> optimizeBoostParameters() {
        try {
            Map<String, Object> optimization = new HashMap<>();
            
            // Analyser l'efficacité des différents types de boost
            Map<String, Double> boostEffectiveness = analyzeBoostEffectiveness();
            
            // Optimiser les paramètres
            optimization.put("boostEffectiveness", boostEffectiveness);
            optimization.put("recommendedSettings", Map.of(
                "premiumBoostLevel", calculateOptimalBoostLevel("PREMIUM", boostEffectiveness),
                "standardBoostLevel", calculateOptimalBoostLevel("STANDARD", boostEffectiveness),
                "urgentBoostLevel", calculateOptimalBoostLevel("URGENT", boostEffectiveness),
                "maxBoostsPerProduct", 3,
                "boostDuration", Map.of(
                    "premium", 7,
                    "standard", 3,
                    "urgent", 1
                )
            ));
            
            return optimization;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation des paramètres de boost: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    // Méthodes utilitaires privées
    
    private String getOptimalAlgorithm(Long userId) {
        // Simulation de sélection d'algorithme optimal
        double random = Math.random();
        if (random > 0.8) return "hybrid";
        if (random > 0.5) return "content-based";
        return "collaborative";
    }
    
    private boolean isBoostPerformingWell(ProductBoost boost) {
        // Simulation de vérification de performance
        return boost.getBoostLevel() > 1 || Math.random() > 0.3;
    }
    
    private void optimizeUserProfile(UserProfile profile) {
        // Simulation d'optimisation de profil
        // En production, on ajusterait les préférences selon les performances
        log.debug("Optimisation du profil utilisateur {}", profile.getId());
    }
    
    private double analyzeContentBasedPerformance(Long userId) {
        // Simulation d'analyse de performance content-based
        return 0.7 + Math.random() * 0.2;
    }
    
    private double analyzeCollaborativePerformance(Long userId) {
        // Simulation d'analyse de performance collaborative
        return 0.6 + Math.random() * 0.3;
    }
    
    private double analyzeHybridPerformance(Long userId) {
        // Simulation d'analyse de performance hybride
        return 0.8 + Math.random() * 0.15;
    }
    
    private String determineOptimalAlgorithm(double contentBased, double collaborative, double hybrid) {
        if (hybrid > 0.8) return "hybrid";
        if (contentBased > collaborative) return "content-based";
        return "collaborative";
    }
    
    private Map<String, Double> analyzeBoostEffectiveness() {
        Map<String, Double> effectiveness = new HashMap<>();
        effectiveness.put("PREMIUM", 0.85 + Math.random() * 0.1);
        effectiveness.put("STANDARD", 0.7 + Math.random() * 0.2);
        effectiveness.put("URGENT", 0.9 + Math.random() * 0.1);
        return effectiveness;
    }
    
    private int calculateOptimalBoostLevel(String boostType, Map<String, Double> effectiveness) {
        Double effectivenessScore = effectiveness.get(boostType);
        if (effectivenessScore == null) return 1;
        
        if (effectivenessScore > 0.8) return 3;
        if (effectivenessScore > 0.6) return 2;
        return 1;
    }
} 