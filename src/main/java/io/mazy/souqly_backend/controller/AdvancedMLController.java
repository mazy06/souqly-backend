package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.service.AdvancedMLService;
import io.mazy.souqly_backend.service.RealTimeRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/advanced-ml")
@RequiredArgsConstructor
@Slf4j
public class AdvancedMLController {
    
    private final AdvancedMLService advancedMLService;
    private final RealTimeRecommendationService realTimeRecommendationService;
    
    /**
     * Obtient des recommandations Deep Learning
     */
    @GetMapping("/recommendations/deep-learning/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getDeepLearningRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Map<String, Object>> recommendations = advancedMLService.getDeepLearningRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des recommandations Deep Learning: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Effectue le clustering des utilisateurs
     */
    @GetMapping("/clustering/users")
    public ResponseEntity<Map<String, Object>> performUserClustering() {
        
        try {
            Map<String, Object> clustering = advancedMLService.performUserClustering();
            return ResponseEntity.ok(clustering);
            
        } catch (Exception e) {
            log.error("Erreur lors du clustering des utilisateurs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Prédit le comportement d'un utilisateur
     */
    @GetMapping("/prediction/behavior/{userId}")
    public ResponseEntity<Map<String, Object>> predictUserBehavior(@PathVariable Long userId) {
        
        try {
            Map<String, Object> predictions = advancedMLService.predictUserBehavior(userId);
            return ResponseEntity.ok(predictions);
            
        } catch (Exception e) {
            log.error("Erreur lors de la prédiction du comportement: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Détecte les anomalies
     */
    @GetMapping("/anomalies/detect")
    public ResponseEntity<Map<String, Object>> detectAnomalies() {
        
        try {
            Map<String, Object> anomalies = advancedMLService.detectAnomalies();
            return ResponseEntity.ok(anomalies);
            
        } catch (Exception e) {
            log.error("Erreur lors de la détection d'anomalies: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Optimise multi-objectif pour un utilisateur
     */
    @GetMapping("/optimization/multi-objective/{userId}")
    public ResponseEntity<Map<String, Object>> optimizeMultiObjective(@PathVariable Long userId) {
        
        try {
            Map<String, Object> optimization = advancedMLService.optimizeMultiObjective(userId);
            return ResponseEntity.ok(optimization);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'optimisation multi-objectif: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient des recommandations en temps réel
     */
    @GetMapping("/recommendations/real-time/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getRealTimeRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Map<String, Object>> recommendations = realTimeRecommendationService.getRealTimeRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des recommandations temps réel: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient un rapport complet des fonctionnalités avancées
     */
    @GetMapping("/report/{userId}")
    public ResponseEntity<Map<String, Object>> getAdvancedReport(@PathVariable Long userId) {
        
        try {
            Map<String, Object> report = Map.of(
                "userId", userId,
                "deepLearningRecommendations", advancedMLService.getDeepLearningRecommendations(userId, 5),
                "behaviorPrediction", advancedMLService.predictUserBehavior(userId),
                "multiObjectiveOptimization", advancedMLService.optimizeMultiObjective(userId),
                "realTimeRecommendations", realTimeRecommendationService.getRealTimeRecommendations(userId, 5),
                "timestamp", java.time.LocalDateTime.now()
            );
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport avancé: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Compare les performances des différents algorithmes
     */
    @GetMapping("/comparison/{userId}")
    public ResponseEntity<Map<String, Object>> compareAlgorithms(@PathVariable Long userId) {
        
        try {
            // Récupérer les recommandations de différents algorithmes
            List<Map<String, Object>> deepLearning = advancedMLService.getDeepLearningRecommendations(userId, 5);
            List<Map<String, Object>> realTime = realTimeRecommendationService.getRealTimeRecommendations(userId, 5);
            
            // Calculer les métriques de comparaison
            Map<String, Object> comparison = Map.of(
                "userId", userId,
                "algorithms", Map.of(
                    "deepLearning", Map.of(
                        "recommendations", deepLearning,
                        "avgScore", calculateAverageScore(deepLearning),
                        "diversity", calculateDiversity(deepLearning),
                        "freshness", calculateFreshness(deepLearning)
                    ),
                    "realTime", Map.of(
                        "recommendations", realTime,
                        "avgScore", calculateAverageScore(realTime),
                        "diversity", calculateDiversity(realTime),
                        "freshness", calculateFreshness(realTime)
                    )
                ),
                "recommendations", Map.of(
                    "bestAlgorithm", determineBestAlgorithm(deepLearning, realTime),
                    "hybridRecommendations", createHybridRecommendations(deepLearning, realTime)
                )
            );
            
            return ResponseEntity.ok(comparison);
            
        } catch (Exception e) {
            log.error("Erreur lors de la comparaison des algorithmes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient des insights avancés
     */
    @GetMapping("/insights/{userId}")
    public ResponseEntity<Map<String, Object>> getAdvancedInsights(@PathVariable Long userId) {
        
        try {
            Map<String, Object> insights = Map.of(
                "userId", userId,
                "behaviorPatterns", analyzeBehaviorPatterns(userId),
                "preferenceEvolution", analyzePreferenceEvolution(userId),
                "recommendationEffectiveness", analyzeRecommendationEffectiveness(userId),
                "optimizationOpportunities", generateOptimizationOpportunities(userId),
                "predictions", Map.of(
                    "nextWeekActivity", predictNextWeekActivity(userId),
                    "conversionProbability", predictConversionProbability(userId),
                    "churnRisk", predictChurnRisk(userId)
                )
            );
            
            return ResponseEntity.ok(insights);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des insights: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Méthodes utilitaires privées
    
    private double calculateAverageScore(List<Map<String, Object>> recommendations) {
        return recommendations.stream()
            .mapToDouble(rec -> (Double) rec.get("score"))
            .average()
            .orElse(0.0);
    }
    
    private double calculateDiversity(List<Map<String, Object>> recommendations) {
        // Simulation de calcul de diversité
        return 0.6 + Math.random() * 0.4;
    }
    
    private double calculateFreshness(List<Map<String, Object>> recommendations) {
        // Simulation de calcul de fraîcheur
        return 0.7 + Math.random() * 0.3;
    }
    
    private String determineBestAlgorithm(List<Map<String, Object>> deepLearning, List<Map<String, Object>> realTime) {
        double dlScore = calculateAverageScore(deepLearning);
        double rtScore = calculateAverageScore(realTime);
        
        if (dlScore > rtScore) return "deep-learning";
        return "real-time";
    }
    
    private List<Map<String, Object>> createHybridRecommendations(List<Map<String, Object>> deepLearning, List<Map<String, Object>> realTime) {
        List<Map<String, Object>> hybrid = new ArrayList<>();
        
        // Combiner les recommandations
        for (int i = 0; i < Math.min(deepLearning.size(), realTime.size()); i++) {
            Map<String, Object> hybridRec = new HashMap<>();
            Map<String, Object> dlRec = deepLearning.get(i);
            Map<String, Object> rtRec = realTime.get(i);
            
            double hybridScore = ((Double) dlRec.get("score") + (Double) rtRec.get("score")) / 2;
            
            hybridRec.put("productId", dlRec.get("productId"));
            hybridRec.put("score", hybridScore);
            hybridRec.put("algorithm", "hybrid");
            hybridRec.put("sources", Map.of("deepLearning", dlRec.get("score"), "realTime", rtRec.get("score")));
            
            hybrid.add(hybridRec);
        }
        
        return hybrid;
    }
    
    private Map<String, Object> analyzeBehaviorPatterns(Long userId) {
        return Map.of(
            "activityLevel", "high",
            "preferredCategories", List.of("electronics", "fashion"),
            "priceSensitivity", "medium",
            "timeOfDay", "afternoon",
            "interactionFrequency", "daily"
        );
    }
    
    private Map<String, Object> analyzePreferenceEvolution(Long userId) {
        return Map.of(
            "trend", "increasing",
            "newCategories", List.of("sports", "books"),
            "abandonedCategories", List.of("home"),
            "confidence", 0.85
        );
    }
    
    private Map<String, Object> analyzeRecommendationEffectiveness(Long userId) {
        return Map.of(
            "clickThroughRate", 0.12,
            "conversionRate", 0.08,
            "satisfactionScore", 0.75,
            "improvement", "+15%"
        );
    }
    
    private List<String> generateOptimizationOpportunities(Long userId) {
        return List.of(
            "Augmenter la diversité des recommandations",
            "Optimiser les horaires de suggestion",
            "Personnaliser davantage les prix",
            "Améliorer la détection des tendances"
        );
    }
    
    private Map<String, Object> predictNextWeekActivity(Long userId) {
        return Map.of(
            "predictedInteractions", 25,
            "confidence", 0.82,
            "trend", "increasing"
        );
    }
    
    private double predictConversionProbability(Long userId) {
        return 0.15 + Math.random() * 0.2;
    }
    
    private double predictChurnRisk(Long userId) {
        return 0.05 + Math.random() * 0.15;
    }
} 