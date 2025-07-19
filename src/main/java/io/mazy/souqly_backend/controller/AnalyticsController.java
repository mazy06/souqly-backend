package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * Obtient le CTR des recommandations pour un utilisateur
     */
    @GetMapping("/recommendations/ctr/{userId}")
    public ResponseEntity<Map<String, Object>> getRecommendationCTR(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            double ctr = analyticsService.calculateRecommendationCTR(userId, startDate, endDate);
            Map<String, Object> response = Map.of(
                "userId", userId,
                "startDate", startDate,
                "endDate", endDate,
                "ctr", ctr,
                "ctrPercentage", String.format("%.2f%%", ctr * 100)
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul du CTR: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient l'efficacité des boosts
     */
    @GetMapping("/boosts/effectiveness")
    public ResponseEntity<Map<String, Object>> getBoostEffectiveness(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            Map<String, Object> effectiveness = analyticsService.calculateBoostEffectiveness(startDate, endDate);
            return ResponseEntity.ok(effectiveness);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de l'efficacité des boosts: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient la précision des recommandations pour un utilisateur
     */
    @GetMapping("/recommendations/accuracy/{userId}")
    public ResponseEntity<Map<String, Object>> getRecommendationAccuracy(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            double accuracy = analyticsService.calculateRecommendationAccuracy(userId, startDate, endDate);
            Map<String, Object> response = Map.of(
                "userId", userId,
                "startDate", startDate,
                "endDate", endDate,
                "accuracy", accuracy,
                "accuracyPercentage", String.format("%.2f%%", accuracy * 100)
            );
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de la précision: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient les métriques globales
     */
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            Map<String, Object> metrics = analyticsService.calculateGlobalMetrics(startDate, endDate);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des métriques globales: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient les tendances temporelles
     */
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            Map<String, Object> trends = analyticsService.calculateTrends(startDate, endDate);
            return ResponseEntity.ok(trends);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des tendances: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient la performance des algorithmes
     */
    @GetMapping("/algorithms/performance")
    public ResponseEntity<Map<String, Object>> getAlgorithmPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            Map<String, Object> performance = analyticsService.calculateAlgorithmPerformance(startDate, endDate);
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul de la performance des algorithmes: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient un rapport complet d'analytics
     */
    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getCompleteReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long userId) {
        
        try {
            Map<String, Object> report = Map.of(
                "period", Map.of(
                    "startDate", startDate,
                    "endDate", endDate
                ),
                "globalMetrics", analyticsService.calculateGlobalMetrics(startDate, endDate),
                "boostEffectiveness", analyticsService.calculateBoostEffectiveness(startDate, endDate),
                "algorithmPerformance", analyticsService.calculateAlgorithmPerformance(startDate, endDate),
                "trends", analyticsService.calculateTrends(startDate, endDate)
            );
            
            // Ajouter les métriques utilisateur si spécifié
            if (userId != null) {
                report = Map.of(
                    "period", Map.of(
                        "startDate", startDate,
                        "endDate", endDate
                    ),
                    "globalMetrics", analyticsService.calculateGlobalMetrics(startDate, endDate),
                    "boostEffectiveness", analyticsService.calculateBoostEffectiveness(startDate, endDate),
                    "algorithmPerformance", analyticsService.calculateAlgorithmPerformance(startDate, endDate),
                    "trends", analyticsService.calculateTrends(startDate, endDate),
                    "userMetrics", Map.of(
                        "ctr", analyticsService.calculateRecommendationCTR(userId, startDate, endDate),
                        "accuracy", analyticsService.calculateRecommendationAccuracy(userId, startDate, endDate)
                    )
                );
            }
            
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 