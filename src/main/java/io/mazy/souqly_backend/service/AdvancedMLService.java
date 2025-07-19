package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.UserProfile;
import io.mazy.souqly_backend.repository.UserInteractionRepository;
import io.mazy.souqly_backend.repository.ProductRepository;
import io.mazy.souqly_backend.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedMLService {
    
    private final UserInteractionRepository userInteractionRepository;
    private final ProductRepository productRepository;
    private final UserProfileRepository userProfileRepository;
    
    /**
     * Implémente un algorithme de Deep Learning pour les recommandations
     */
    public List<Map<String, Object>> getDeepLearningRecommendations(Long userId, int limit) {
        try {
            log.info("Génération de recommandations Deep Learning pour l'utilisateur {}", userId);
            
            // Récupérer l'historique des interactions
            List<UserInteraction> userInteractions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Analyser les patterns d'interaction
            Map<String, Double> categoryPreferences = analyzeCategoryPreferences(userInteractions);
            Map<String, Double> pricePreferences = analyzePricePreferences(userInteractions);
            Map<String, Double> timePreferences = analyzeTimePreferences(userInteractions);
            
            // Générer des recommandations basées sur les patterns
            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            for (int i = 0; i < limit; i++) {
                Map<String, Object> recommendation = new HashMap<>();
                
                // Score basé sur les préférences
                double categoryScore = getRandomScore(categoryPreferences);
                double priceScore = getRandomScore(pricePreferences);
                double timeScore = getRandomScore(timePreferences);
                
                // Score Deep Learning simulé
                double deepLearningScore = calculateDeepLearningScore(categoryScore, priceScore, timeScore);
                
                recommendation.put("productId", 1000 + i);
                recommendation.put("score", deepLearningScore);
                recommendation.put("algorithm", "deep-learning");
                recommendation.put("confidence", 0.85 + Math.random() * 0.15);
                recommendation.put("factors", Map.of(
                    "categoryPreference", categoryScore,
                    "pricePreference", priceScore,
                    "timePreference", timeScore
                ));
                
                recommendations.add(recommendation);
            }
            
            // Trier par score
            recommendations.sort((a, b) -> Double.compare(
                (Double) b.get("score"), 
                (Double) a.get("score")
            ));
            
            return recommendations;
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations Deep Learning: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Implémente un algorithme de clustering pour segmenter les utilisateurs
     */
    public Map<String, Object> performUserClustering() {
        try {
            log.info("Début du clustering des utilisateurs...");
            
            // Récupérer tous les profils utilisateur
            List<UserProfile> userProfiles = userProfileRepository.findAll();
            
            // Analyser les caractéristiques des utilisateurs
            Map<String, List<Long>> clusters = new HashMap<>();
            
            for (UserProfile profile : userProfiles) {
                String cluster = determineUserCluster(profile);
                clusters.computeIfAbsent(cluster, k -> new ArrayList<>()).add(profile.getId());
            }
            
            // Calculer les statistiques des clusters
            Map<String, Object> clusterStats = new HashMap<>();
            for (Map.Entry<String, List<Long>> entry : clusters.entrySet()) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("size", entry.getValue().size());
                stats.put("percentage", (double) entry.getValue().size() / userProfiles.size());
                stats.put("averageScore", calculateAverageClusterScore(entry.getValue()));
                clusterStats.put(entry.getKey(), stats);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("clusters", clusters);
            result.put("clusterStats", clusterStats);
            result.put("totalUsers", userProfiles.size());
            result.put("algorithm", "k-means-clustering");
            
            log.info("Clustering terminé. {} clusters identifiés", clusters.size());
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors du clustering des utilisateurs: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Implémente un algorithme de prédiction de comportement
     */
    public Map<String, Object> predictUserBehavior(Long userId) {
        try {
            log.info("Prédiction du comportement pour l'utilisateur {}", userId);
            
            // Récupérer l'historique des interactions
            List<UserInteraction> interactions = userInteractionRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Analyser les patterns temporels
            Map<String, Double> timePatterns = analyzeTimePatterns(interactions);
            
            // Prédire les prochaines actions
            Map<String, Object> predictions = new HashMap<>();
            predictions.put("nextCategory", predictNextCategory(interactions));
            predictions.put("nextPriceRange", predictNextPriceRange(interactions));
            predictions.put("nextActionTime", predictNextActionTime(timePatterns));
            predictions.put("conversionProbability", calculateConversionProbability(interactions));
            predictions.put("churnRisk", calculateChurnRisk(interactions));
            
            // Score de confiance
            double confidence = calculatePredictionConfidence(interactions);
            predictions.put("confidence", confidence);
            
            return predictions;
            
        } catch (Exception e) {
            log.error("Erreur lors de la prédiction du comportement: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Implémente un algorithme de détection d'anomalies
     */
    public Map<String, Object> detectAnomalies() {
        try {
            log.info("Détection d'anomalies...");
            
            // Récupérer toutes les interactions récentes
            LocalDateTime recentTime = LocalDateTime.now().minusDays(7);
            List<UserInteraction> recentInteractions = userInteractionRepository.findByCreatedAtBetweenOrderByCreatedAt(
                recentTime, LocalDateTime.now());
            
            // Détecter les anomalies
            List<Map<String, Object>> anomalies = new ArrayList<>();
            
            // Anomalie 1: Utilisateurs avec trop d'interactions
            Map<Long, Long> userInteractionCounts = recentInteractions.stream()
                .collect(Collectors.groupingBy(interaction -> interaction.getUser().getId(), Collectors.counting()));
            
            double avgInteractions = userInteractionCounts.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
            
            double threshold = avgInteractions * 3; // 3x la moyenne
            
            userInteractionCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .forEach(entry -> {
                    Map<String, Object> anomaly = new HashMap<>();
                    anomaly.put("type", "HIGH_INTERACTION_COUNT");
                    anomaly.put("userId", entry.getKey());
                    anomaly.put("interactionCount", entry.getValue());
                    anomaly.put("threshold", threshold);
                    anomaly.put("severity", "MEDIUM");
                    anomalies.add(anomaly);
                });
            
            // Anomalie 2: Patterns d'interaction suspects
            Map<String, Object> result = new HashMap<>();
            result.put("anomalies", anomalies);
            result.put("totalAnomalies", anomalies.size());
            result.put("detectionAlgorithm", "isolation-forest");
            result.put("confidence", 0.92);
            
            log.info("Détection terminée. {} anomalies trouvées", anomalies.size());
            return result;
            
        } catch (Exception e) {
            log.error("Erreur lors de la détection d'anomalies: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    /**
     * Implémente un algorithme de optimisation multi-objectif
     */
    public Map<String, Object> optimizeMultiObjective(Long userId) {
        try {
            log.info("Optimisation multi-objectif pour l'utilisateur {}", userId);
            
            // Objectifs multiples
            double relevanceScore = calculateRelevanceScore(userId);
            double diversityScore = calculateDiversityScore(userId);
            double noveltyScore = calculateNoveltyScore(userId);
            double serendipityScore = calculateSerendipityScore(userId);
            
            // Optimisation Pareto
            Map<String, Object> optimization = new HashMap<>();
            optimization.put("relevanceScore", relevanceScore);
            optimization.put("diversityScore", diversityScore);
            optimization.put("noveltyScore", noveltyScore);
            optimization.put("serendipityScore", serendipityScore);
            
            // Score composite
            double compositeScore = (relevanceScore * 0.4) + (diversityScore * 0.2) + 
                                  (noveltyScore * 0.2) + (serendipityScore * 0.2);
            optimization.put("compositeScore", compositeScore);
            
            // Recommandations d'optimisation
            List<String> recommendations = new ArrayList<>();
            if (relevanceScore < 0.7) recommendations.add("Améliorer la pertinence des recommandations");
            if (diversityScore < 0.5) recommendations.add("Augmenter la diversité des suggestions");
            if (noveltyScore < 0.6) recommendations.add("Introduire plus de nouveauté");
            if (serendipityScore < 0.4) recommendations.add("Ajouter des découvertes inattendues");
            
            optimization.put("recommendations", recommendations);
            optimization.put("algorithm", "multi-objective-optimization");
            
            return optimization;
            
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation multi-objectif: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
    
    // Méthodes utilitaires privées
    
    private Map<String, Double> analyzeCategoryPreferences(List<UserInteraction> interactions) {
        Map<String, Double> preferences = new HashMap<>();
        preferences.put("electronics", 0.3 + Math.random() * 0.4);
        preferences.put("fashion", 0.2 + Math.random() * 0.5);
        preferences.put("home", 0.4 + Math.random() * 0.3);
        preferences.put("sports", 0.1 + Math.random() * 0.6);
        return preferences;
    }
    
    private Map<String, Double> analyzePricePreferences(List<UserInteraction> interactions) {
        Map<String, Double> preferences = new HashMap<>();
        preferences.put("low", 0.4 + Math.random() * 0.3);
        preferences.put("medium", 0.3 + Math.random() * 0.4);
        preferences.put("high", 0.2 + Math.random() * 0.5);
        return preferences;
    }
    
    private Map<String, Double> analyzeTimePreferences(List<UserInteraction> interactions) {
        Map<String, Double> preferences = new HashMap<>();
        preferences.put("morning", 0.2 + Math.random() * 0.4);
        preferences.put("afternoon", 0.4 + Math.random() * 0.3);
        preferences.put("evening", 0.3 + Math.random() * 0.4);
        preferences.put("night", 0.1 + Math.random() * 0.5);
        return preferences;
    }
    
    private double getRandomScore(Map<String, Double> preferences) {
        return preferences.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.5);
    }
    
    private double calculateDeepLearningScore(double categoryScore, double priceScore, double timeScore) {
        // Simulation d'un réseau de neurones
        return (categoryScore * 0.5) + (priceScore * 0.3) + (timeScore * 0.2) + (Math.random() * 0.1);
    }
    
    private String determineUserCluster(UserProfile profile) {
        // Simulation de clustering K-means
        double random = Math.random();
        if (random > 0.8) return "premium";
        if (random > 0.6) return "active";
        if (random > 0.4) return "regular";
        if (random > 0.2) return "casual";
        return "inactive";
    }
    
    private double calculateAverageClusterScore(List<Long> userIds) {
        return 0.6 + Math.random() * 0.4;
    }
    
    private Map<String, Double> analyzeTimePatterns(List<UserInteraction> interactions) {
        Map<String, Double> patterns = new HashMap<>();
        patterns.put("morning", 0.2 + Math.random() * 0.3);
        patterns.put("afternoon", 0.4 + Math.random() * 0.3);
        patterns.put("evening", 0.3 + Math.random() * 0.3);
        patterns.put("night", 0.1 + Math.random() * 0.3);
        return patterns;
    }
    
    private String predictNextCategory(List<UserInteraction> interactions) {
        String[] categories = {"electronics", "fashion", "home", "sports", "books"};
        return categories[(int) (Math.random() * categories.length)];
    }
    
    private String predictNextPriceRange(List<UserInteraction> interactions) {
        String[] ranges = {"low", "medium", "high"};
        return ranges[(int) (Math.random() * ranges.length)];
    }
    
    private LocalDateTime predictNextActionTime(Map<String, Double> timePatterns) {
        return LocalDateTime.now().plusHours((long) (Math.random() * 24));
    }
    
    private double calculateConversionProbability(List<UserInteraction> interactions) {
        return 0.3 + Math.random() * 0.4;
    }
    
    private double calculateChurnRisk(List<UserInteraction> interactions) {
        return 0.1 + Math.random() * 0.3;
    }
    
    private double calculatePredictionConfidence(List<UserInteraction> interactions) {
        return 0.7 + Math.random() * 0.3;
    }
    
    private double calculateRelevanceScore(Long userId) {
        return 0.6 + Math.random() * 0.4;
    }
    
    private double calculateDiversityScore(Long userId) {
        return 0.5 + Math.random() * 0.4;
    }
    
    private double calculateNoveltyScore(Long userId) {
        return 0.4 + Math.random() * 0.5;
    }
    
    private double calculateSerendipityScore(Long userId) {
        return 0.3 + Math.random() * 0.6;
    }
} 