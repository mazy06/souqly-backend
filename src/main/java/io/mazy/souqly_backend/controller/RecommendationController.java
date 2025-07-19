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

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {
    
    private final ContentBasedRecommendationService contentBasedService;
    private final CollaborativeFilteringService collaborativeService;
    private final UserService userService;
    
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
    public ResponseEntity<List<Product>> getRecommendationsForMe(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "hybrid") String type) {
        
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
            
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération des recommandations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 