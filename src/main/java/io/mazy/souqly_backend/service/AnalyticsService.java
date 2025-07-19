package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.ProductBoost;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.ProductBoostRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final UserInteractionRepository userInteractionRepository;
    private final ProductBoostRepository productBoostRepository;
    private final ProductRepository productRepository;
    
    /**
     * Calcule le taux de clic (CTR) des recommandations
     */
    public double calculateRecommendationCTR(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Récupérer toutes les interactions de l'utilisateur
            List<UserInteraction> interactions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Filtrer par période
            List<UserInteraction> periodInteractions = interactions.stream()
                .filter(interaction -> {
                    LocalDateTime createdAt = interaction.getCreatedAt();
                    return createdAt.isAfter(startDate) && createdAt.isBefore(endDate);
                })
                .collect(Collectors.toList());
            
            // Compter les vues et clics
            long views = periodInteractions.stream()
                .filter(interaction -> interaction.getInteractionType() == UserInteraction.InteractionType.VIEW)
                .count();
            
            long clicks = periodInteractions.stream()
                .filter(interaction -> interaction.getInteractionType() == UserInteraction.InteractionType.CLICK)
                .count();
            
            if (views == 0) return 0.0;
            return (double) clicks / views;
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul du CTR: {}", e.getMessage(), e);
            return 0.0;
        }
    }
    
    /**
     * Calcule l'efficacité des boosts
     */
    public Map<String, Object> calculateBoostEffectiveness(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Récupérer tous les boosts actifs dans la période
            List<ProductBoost> activeBoosts = productBoostRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                startDate, endDate);
            
            Map<String, Object> results = new HashMap<>();
            Map<String, List<ProductBoost>> boostsByType = activeBoosts.stream()
                .collect(Collectors.groupingBy(boost -> boost.getBoostType().name()));
            
            for (Map.Entry<String, List<ProductBoost>> entry : boostsByType.entrySet()) {
                String boostType = entry.getKey();
                List<ProductBoost> boosts = entry.getValue();
                
                // Calculer les métriques pour chaque type de boost
                double avgViews = calculateAverageViews(boosts, startDate, endDate);
                double avgClicks = calculateAverageClicks(boosts, startDate, endDate);
                double avgFavorites = calculateAverageFavorites(boosts, startDate, endDate);
                double ctr = avgViews > 0 ? avgClicks / avgViews : 0.0;
                
                Map<String, Object> boostMetrics = new HashMap<>();
                boostMetrics.put("count", boosts.size());
                boostMetrics.put("avgViews", avgViews);
                boostMetrics.put("avgClicks", avgClicks);
                boostMetrics.put("avgFavorites", avgFavorites);
                boostMetrics.put("ctr", ctr);
                
                results.put(boostType, boostMetrics);
            }
            
            return results;
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de l'efficacité des boosts: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Calcule la précision des recommandations
     */
    public double calculateRecommendationAccuracy(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Récupérer les interactions de l'utilisateur
            List<UserInteraction> interactions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Filtrer par période
            List<UserInteraction> periodInteractions = interactions.stream()
                .filter(interaction -> {
                    LocalDateTime createdAt = interaction.getCreatedAt();
                    return createdAt.isAfter(startDate) && createdAt.isBefore(endDate);
                })
                .collect(Collectors.toList());
            
            // Compter les interactions positives (clics, favoris)
            long positiveInteractions = periodInteractions.stream()
                .filter(interaction -> 
                    interaction.getInteractionType() == UserInteraction.InteractionType.CLICK ||
                    interaction.getInteractionType() == UserInteraction.InteractionType.FAVORITE)
                .count();
            
            long totalInteractions = periodInteractions.size();
            
            if (totalInteractions == 0) return 0.0;
            return (double) positiveInteractions / totalInteractions;
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de la précision: {}", e.getMessage(), e);
            return 0.0;
        }
    }
    
    /**
     * Calcule les métriques de performance globales
     */
    public Map<String, Object> calculateGlobalMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // Nombre total d'interactions
            long totalInteractions = userInteractionRepository.countByCreatedAtBetween(startDate, endDate);
            metrics.put("totalInteractions", totalInteractions);
            
            // Répartition par type d'interaction
            Map<UserInteraction.InteractionType, Long> interactionsByType = new HashMap<>();
            for (UserInteraction.InteractionType type : UserInteraction.InteractionType.values()) {
                long count = userInteractionRepository.countByInteractionTypeAndCreatedAtBetween(type, startDate, endDate);
                interactionsByType.put(type, count);
            }
            metrics.put("interactionsByType", interactionsByType);
            
            // Nombre de produits boostés actifs
            long activeBoosts = productBoostRepository.countByIsActiveTrueAndStartDateBeforeAndEndDateAfter(
                startDate, endDate);
            metrics.put("activeBoosts", activeBoosts);
            
            // Nombre d'utilisateurs actifs
            long activeUsers = userInteractionRepository.countDistinctUserIdByCreatedAtBetween(startDate, endDate);
            metrics.put("activeUsers", activeUsers);
            
            // Taux de conversion moyen
            long views = interactionsByType.getOrDefault(UserInteraction.InteractionType.VIEW, 0L);
            long clicks = interactionsByType.getOrDefault(UserInteraction.InteractionType.CLICK, 0L);
            double conversionRate = views > 0 ? (double) clicks / views : 0.0;
            metrics.put("conversionRate", conversionRate);
            
            return metrics;
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des métriques globales: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Calcule les tendances temporelles
     */
    public Map<String, Object> calculateTrends(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> trends = new HashMap<>();
            
            // Grouper les interactions par jour
            Map<String, Long> dailyInteractions = new HashMap<>();
            Map<String, Long> dailyViews = new HashMap<>();
            Map<String, Long> dailyClicks = new HashMap<>();
            
            List<UserInteraction> interactions = userInteractionRepository.findByCreatedAtBetweenOrderByCreatedAt(startDate, endDate);
            
            for (UserInteraction interaction : interactions) {
                String dateKey = interaction.getCreatedAt().toLocalDate().toString();
                
                dailyInteractions.merge(dateKey, 1L, Long::sum);
                
                if (interaction.getInteractionType() == UserInteraction.InteractionType.VIEW) {
                    dailyViews.merge(dateKey, 1L, Long::sum);
                } else if (interaction.getInteractionType() == UserInteraction.InteractionType.CLICK) {
                    dailyClicks.merge(dateKey, 1L, Long::sum);
                }
            }
            
            trends.put("dailyInteractions", dailyInteractions);
            trends.put("dailyViews", dailyViews);
            trends.put("dailyClicks", dailyClicks);
            
            return trends;
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des tendances: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Calcule les métriques de performance des algorithmes
     */
    public Map<String, Object> calculateAlgorithmPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Map<String, Object> performance = new HashMap<>();
            
            // Métriques pour content-based
            double contentBasedAccuracy = calculateAlgorithmAccuracy("content-based", startDate, endDate);
            performance.put("contentBasedAccuracy", contentBasedAccuracy);
            
            // Métriques pour collaborative
            double collaborativeAccuracy = calculateAlgorithmAccuracy("collaborative", startDate, endDate);
            performance.put("collaborativeAccuracy", collaborativeAccuracy);
            
            // Métriques pour hybrid
            double hybridAccuracy = calculateAlgorithmAccuracy("hybrid", startDate, endDate);
            performance.put("hybridAccuracy", hybridAccuracy);
            
            // Temps de réponse moyen
            Map<String, Double> responseTimes = calculateAverageResponseTimes(startDate, endDate);
            performance.put("responseTimes", responseTimes);
            
            return performance;
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de la performance des algorithmes: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    // Méthodes utilitaires privées
    private double calculateAverageViews(List<ProductBoost> boosts, LocalDateTime startDate, LocalDateTime endDate) {
        if (boosts.isEmpty()) return 0.0;
        
        long totalViews = boosts.stream()
            .mapToLong(boost -> {
                Long productId = boost.getProduct().getId();
                return userInteractionRepository.countByProductIdAndInteractionTypeAndCreatedAtBetween(
                    productId, UserInteraction.InteractionType.VIEW, startDate, endDate);
            })
            .sum();
        
        return (double) totalViews / boosts.size();
    }
    
    private double calculateAverageClicks(List<ProductBoost> boosts, LocalDateTime startDate, LocalDateTime endDate) {
        if (boosts.isEmpty()) return 0.0;
        
        long totalClicks = boosts.stream()
            .mapToLong(boost -> {
                Long productId = boost.getProduct().getId();
                return userInteractionRepository.countByProductIdAndInteractionTypeAndCreatedAtBetween(
                    productId, UserInteraction.InteractionType.CLICK, startDate, endDate);
            })
            .sum();
        
        return (double) totalClicks / boosts.size();
    }
    
    private double calculateAverageFavorites(List<ProductBoost> boosts, LocalDateTime startDate, LocalDateTime endDate) {
        if (boosts.isEmpty()) return 0.0;
        
        long totalFavorites = boosts.stream()
            .mapToLong(boost -> {
                Long productId = boost.getProduct().getId();
                return userInteractionRepository.countByProductIdAndInteractionTypeAndCreatedAtBetween(
                    productId, UserInteraction.InteractionType.FAVORITE, startDate, endDate);
            })
            .sum();
        
        return (double) totalFavorites / boosts.size();
    }
    
    private double calculateAlgorithmAccuracy(String algorithmType, LocalDateTime startDate, LocalDateTime endDate) {
        // Simulation de calcul de précision par algorithme
        // En production, il faudrait stocker quel algorithme a été utilisé pour chaque recommandation
        switch (algorithmType) {
            case "content-based":
                return 0.75 + Math.random() * 0.15; // 75-90%
            case "collaborative":
                return 0.70 + Math.random() * 0.20; // 70-90%
            case "hybrid":
                return 0.80 + Math.random() * 0.15; // 80-95%
            default:
                return 0.0;
        }
    }
    
    private Map<String, Double> calculateAverageResponseTimes(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Double> responseTimes = new HashMap<>();
        
        // Temps de réponse simulés (en millisecondes)
        responseTimes.put("contentBased", 150.0 + Math.random() * 100);
        responseTimes.put("collaborative", 200.0 + Math.random() * 150);
        responseTimes.put("hybrid", 250.0 + Math.random() * 200);
        
        return responseTimes;
    }
} 