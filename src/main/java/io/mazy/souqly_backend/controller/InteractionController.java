package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.entity.UserInteraction;
import io.mazy.souqly_backend.service.InteractionTrackingService;
import io.mazy.souqly_backend.service.ProductService;
import io.mazy.souqly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Slf4j
public class InteractionController {
    
    private final InteractionTrackingService interactionTrackingService;
    private final ProductService productService;
    private final UserService userService;
    
    /**
     * Enregistre une vue de produit
     */
    @PostMapping("/track/view")
    public ResponseEntity<String> trackProductView(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        
        try {
            Long productId = Long.valueOf(requestBody.get("productId").toString());
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            
            var userOpt = userService.getUserById(userId);
            var productOpt = productService.getProduct(productId);
            
            if (userOpt.isEmpty() || productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User or product not found");
            }
            
            var user = userOpt.get();
            var product = productOpt.get();
            
            String sessionId = requestBody.containsKey("sessionId") ? 
                requestBody.get("sessionId").toString() : request.getSession().getId();
            String ipAddress = getClientIpAddress(request);
            String userAgent = requestBody.containsKey("userAgent") ? 
                requestBody.get("userAgent").toString() : request.getHeader("User-Agent");
            
            interactionTrackingService.trackProductView(user, product, sessionId, ipAddress, userAgent);
            
            return ResponseEntity.ok("View tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking product view: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error tracking view");
        }
    }
    
    /**
     * Enregistre un ajout aux favoris
     */
    @PostMapping("/track/favorite")
    public ResponseEntity<String> trackFavorite(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        
        try {
            Long productId = Long.valueOf(requestBody.get("productId").toString());
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            
            var userOpt = userService.getUserById(userId);
            var productOpt = productService.getProduct(productId);
            
            if (userOpt.isEmpty() || productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User or product not found");
            }
            
            var user = userOpt.get();
            var product = productOpt.get();
            
            String sessionId = requestBody.containsKey("sessionId") ? 
                requestBody.get("sessionId").toString() : request.getSession().getId();
            String ipAddress = getClientIpAddress(request);
            String userAgent = requestBody.containsKey("userAgent") ? 
                requestBody.get("userAgent").toString() : request.getHeader("User-Agent");
            
            interactionTrackingService.trackFavorite(user, product, sessionId, ipAddress, userAgent);
            
            return ResponseEntity.ok("Favorite tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking favorite: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error tracking favorite");
        }
    }
    
    /**
     * Enregistre une recherche
     */
    @PostMapping("/track/search")
    public ResponseEntity<String> trackSearch(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        
        try {
            String query = requestBody.get("query").toString();
            Long userId = Long.valueOf(requestBody.get("userId").toString());
            
            var userOpt = userService.getUserById(userId);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            var user = userOpt.get();
            
            String sessionId = requestBody.containsKey("sessionId") ? 
                requestBody.get("sessionId").toString() : request.getSession().getId();
            String ipAddress = getClientIpAddress(request);
            String userAgent = requestBody.containsKey("userAgent") ? 
                requestBody.get("userAgent").toString() : request.getHeader("User-Agent");
            
            interactionTrackingService.trackSearch(user, query, sessionId, ipAddress, userAgent);
            
            return ResponseEntity.ok("Search tracked successfully");
        } catch (Exception e) {
            log.error("Error tracking search: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error tracking search");
        }
    }
    
    /**
     * Récupère les interactions d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserInteraction>> getUserInteractions(@PathVariable Long userId) {
        try {
            List<UserInteraction> interactions = interactionTrackingService.getUserInteractions(userId);
            return ResponseEntity.ok(interactions);
        } catch (Exception e) {
            log.error("Error getting user interactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Récupère les interactions pour un produit
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<UserInteraction>> getProductInteractions(@PathVariable Long productId) {
        try {
            List<UserInteraction> interactions = interactionTrackingService.getProductInteractions(productId);
            return ResponseEntity.ok(interactions);
        } catch (Exception e) {
            log.error("Error getting product interactions: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Méthode utilitaire pour récupérer l'adresse IP du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 