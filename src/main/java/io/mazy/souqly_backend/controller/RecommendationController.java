package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.service.ContentBasedRecommendationService;
import io.mazy.souqly_backend.service.CollaborativeFilteringService;
import io.mazy.souqly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {
    
    private final ContentBasedRecommendationService contentBasedService;
    private final CollaborativeFilteringService collaborativeService;
    private final UserService userService;
    
    /**
     * Endpoint de test simple pour vérifier l'accès
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Recommendation service is running");
        response.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtient des recommandations basées sur le contenu
     */
    @GetMapping("/content-based/{userId}")
    public ResponseEntity<List<Product>> getContentBasedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // Vérifier que l'utilisateur existe
            Optional<UserDto> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Product> recommendations = contentBasedService.getContentBasedRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations content-based: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient des recommandations basées sur la collaboration
     */
    @GetMapping("/collaborative/{userId}")
    public ResponseEntity<List<Product>> getCollaborativeRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // Vérifier que l'utilisateur existe
            Optional<UserDto> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Product> recommendations = collaborativeService.getCollaborativeRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations collaboratives: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient des recommandations hybrides (content-based + collaborative)
     */
    @GetMapping("/hybrid/{userId}")
    public ResponseEntity<List<Product>> getHybridRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            // Vérifier que l'utilisateur existe
            Optional<UserDto> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Product> recommendations = collaborativeService.getHybridRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations hybrides: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtient des recommandations avec métriques détaillées
     */
    @GetMapping("/detailed/{userId}")
    public ResponseEntity<Map<String, Object>> getDetailedRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "hybrid") String type) {
        
        try {
            // Vérifier que l'utilisateur existe
            Optional<UserDto> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Product> recommendations;
            switch (type.toLowerCase()) {
                case "content":
                    recommendations = contentBasedService.getContentBasedRecommendations(userId, limit);
                    break;
                case "collaborative":
                    recommendations = collaborativeService.getCollaborativeRecommendations(userId, limit);
                    break;
                case "hybrid":
                default:
                    recommendations = collaborativeService.getHybridRecommendations(userId, limit);
                    break;
            }
            
            // Calculer les métriques
            Map<String, Object> metrics = calculateRecommendationMetrics(recommendations);
            
            Map<String, Object> response = new HashMap<>();
            response.put("recommendations", recommendations);
            response.put("metrics", metrics);
            response.put("userId", userId);
            response.put("algorithm", type);
            response.put("limit", limit);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations détaillées: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Calcule et met à jour le profil utilisateur
     */
    @PostMapping("/calculate-profile/{userId}")
    public ResponseEntity<String> calculateUserProfile(@PathVariable Long userId) {
        try {
            // Vérifier que l'utilisateur existe
            Optional<UserDto> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }
            
            contentBasedService.calculateUserProfile(userId);
            return ResponseEntity.ok("Profil utilisateur calculé avec succès");
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul du profil utilisateur: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erreur lors du calcul du profil");
        }
    }
    
    /**
     * Calcule les similarités utilisateur
     */
    @PostMapping("/calculate-similarities/{userId}")
    public ResponseEntity<String> calculateUserSimilarities(@PathVariable Long userId) {
        try {
            // Vérifier que l'utilisateur existe
            Optional<UserDto> userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé");
            }
            
            collaborativeService.calculateAndSaveUserSimilarities(userId);
            return ResponseEntity.ok("Similarités utilisateur calculées avec succès");
            
        } catch (Exception e) {
            log.error("Erreur lors du calcul des similarités utilisateur: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erreur lors du calcul des similarités");
        }
    }
    
    /**
     * Obtient des recommandations pour l'utilisateur connecté
     */
    @GetMapping("/for-me")
    public ResponseEntity<Map<String, Object>> getRecommendationsForMe(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "hybrid") String type,
            @RequestParam(defaultValue = "false") boolean includeMetrics) {
        
        try {
            // Récupérer l'utilisateur connecté depuis le contexte de sécurité
            // Pour l'instant, on utilise un ID par défaut
            Long currentUserId = 1L; // TODO: Récupérer depuis le contexte de sécurité
            
            List<Product> recommendations;
            switch (type.toLowerCase()) {
                case "content":
                    recommendations = contentBasedService.getContentBasedRecommendations(currentUserId, limit);
                    break;
                case "collaborative":
                    recommendations = collaborativeService.getCollaborativeRecommendations(currentUserId, limit);
                    break;
                case "hybrid":
                default:
                    recommendations = collaborativeService.getHybridRecommendations(currentUserId, limit);
                    break;
            }
            
            if (includeMetrics) {
                Map<String, Object> metrics = calculateRecommendationMetrics(recommendations);
                Map<String, Object> response = new HashMap<>();
                response.put("recommendations", recommendations);
                response.put("metrics", metrics);
                response.put("userId", currentUserId);
                response.put("algorithm", type);
                response.put("limit", limit);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(Map.of("recommendations", recommendations));
            }
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Teste l'algorithme de recommandation avec des données simulées
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testRecommendationAlgorithm(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "hybrid") String type) {
        
        try {
            Long testUserId = 1L;
            
            List<Product> recommendations;
            switch (type.toLowerCase()) {
                case "content":
                    recommendations = contentBasedService.getContentBasedRecommendations(testUserId, limit);
                    break;
                case "collaborative":
                    recommendations = collaborativeService.getCollaborativeRecommendations(testUserId, limit);
                    break;
                case "hybrid":
                default:
                    recommendations = collaborativeService.getHybridRecommendations(testUserId, limit);
                    break;
            }
            
            Map<String, Object> metrics = calculateRecommendationMetrics(recommendations);
            
            Map<String, Object> response = new HashMap<>();
            response.put("recommendations", recommendations);
            response.put("metrics", metrics);
            response.put("testUserId", testUserId);
            response.put("algorithm", type);
            response.put("limit", limit);
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du test de l'algorithme: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Calcule les métriques des recommandations
     */
    private Map<String, Object> calculateRecommendationMetrics(List<Product> recommendations) {
        Map<String, Object> metrics = new HashMap<>();
        
        if (recommendations.isEmpty()) {
            metrics.put("totalRecommendations", 0);
            metrics.put("boostedCount", 0);
            metrics.put("avgPrice", 0.0);
            metrics.put("avgFavorites", 0.0);
            metrics.put("diversity", 0.0);
            return metrics;
        }
        
        // Compter les produits boostés
        long boostedCount = recommendations.stream()
            .filter(product -> product.getIsBoosted() != null && product.getIsBoosted())
            .count();
        
        // Calculer le prix moyen
        double avgPrice = recommendations.stream()
            .mapToDouble(product -> product.getPrice() != null ? product.getPrice() : 0.0)
            .average()
            .orElse(0.0);
        
        // Calculer les favoris moyens
        double avgFavorites = recommendations.stream()
            .mapToDouble(product -> product.getFavoriteCount() != null ? product.getFavoriteCount() : 0.0)
            .average()
            .orElse(0.0);
        
        // Calculer la diversité (nombre de marques uniques)
        long uniqueBrands = recommendations.stream()
            .map(Product::getBrand)
            .filter(brand -> brand != null && !brand.isEmpty())
            .distinct()
            .count();
        
        double diversity = (double) uniqueBrands / recommendations.size();
        
        metrics.put("totalRecommendations", recommendations.size());
        metrics.put("boostedCount", boostedCount);
        metrics.put("boostedPercentage", (double) boostedCount / recommendations.size() * 100);
        metrics.put("avgPrice", avgPrice);
        metrics.put("avgFavorites", avgFavorites);
        metrics.put("uniqueBrands", uniqueBrands);
        metrics.put("diversity", diversity);
        
        return metrics;
    }
} 