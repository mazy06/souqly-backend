package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.entity.ProductBoost;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.dto.UserDto;
import io.mazy.souqly_backend.service.BoostService;
import io.mazy.souqly_backend.service.ProductService;
import io.mazy.souqly_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/boosts")
@RequiredArgsConstructor
@Slf4j
public class BoostController {
    
    private final BoostService boostService;
    private final ProductService productService;
    private final UserService userService;
    
    /**
     * Récupère les informations de boost pour un produit
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductBoost> getProductBoost(@PathVariable Long productId) {
        try {
            Optional<ProductBoost> boost = boostService.getProductBoost(productId);
            return boost.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du boost: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Récupère tous les boosts actifs
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductBoost>> getActiveBoosts() {
        try {
            List<ProductBoost> boosts = boostService.getActiveBoosts();
            return ResponseEntity.ok(boosts);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des boosts actifs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Récupère les boosts de l'utilisateur connecté
     */
    @GetMapping("/my-boosts")
    public ResponseEntity<List<ProductBoost>> getMyBoosts() {
        try {
            // TODO: Récupérer l'utilisateur connecté depuis le contexte de sécurité
            Long currentUserId = 1L; // Pour l'instant, utiliser un ID par défaut
            
            List<ProductBoost> boosts = boostService.getBoostsByUserId(currentUserId);
            return ResponseEntity.ok(boosts);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de mes boosts: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Crée un nouveau boost
     */
    @PostMapping("/create")
    public ResponseEntity<ProductBoost> createBoost(@RequestBody Map<String, Object> requestBody) {
        try {
            Long productId = Long.valueOf(requestBody.get("productId").toString());
            String boostType = requestBody.get("boostType").toString();
            Integer boostLevel = Integer.valueOf(requestBody.get("boostLevel").toString());
            Integer duration = Integer.valueOf(requestBody.get("duration").toString());
            
            // Vérifier que le produit existe
            Optional<Product> productOpt = productService.getProduct(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Créer le boost
            ProductBoost boost = new ProductBoost();
            boost.setProduct(productOpt.get());
            boost.setBoostType(ProductBoost.BoostType.valueOf(boostType.toUpperCase()));
            boost.setBoostLevel(boostLevel);
            boost.setStartDate(LocalDateTime.now());
            boost.setEndDate(LocalDateTime.now().plusDays(duration));
            boost.setIsActive(true);
            
            ProductBoost savedBoost = boostService.createBoost(boost);
            return ResponseEntity.ok(savedBoost);
            
        } catch (Exception e) {
            log.error("Erreur lors de la création du boost: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Annule un boost
     */
    @PostMapping("/{boostId}/cancel")
    public ResponseEntity<String> cancelBoost(@PathVariable Long boostId) {
        try {
            Optional<ProductBoost> boostOpt = boostService.getBoostById(boostId);
            if (boostOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ProductBoost boost = boostOpt.get();
            boost.setIsActive(false);
            boostService.updateBoost(boost);
            
            return ResponseEntity.ok("Boost annulé avec succès");
            
        } catch (Exception e) {
            log.error("Erreur lors de l'annulation du boost: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erreur lors de l'annulation");
        }
    }
    
    /**
     * Récupère les prix des boosts
     */
    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> getBoostPrices() {
        try {
            Map<String, Double> prices = Map.of(
                "premium", 10.0,
                "standard", 5.0,
                "urgent", 15.0
            );
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des prix: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Récupère les produits boostés
     */
    @GetMapping("/boosted-products")
    public ResponseEntity<List<Product>> getBoostedProducts() {
        try {
            List<Product> boostedProducts = boostService.getBoostedProducts();
            return ResponseEntity.ok(boostedProducts);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des produits boostés: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Vérifie si un produit est boosté
     */
    @GetMapping("/product/{productId}/is-boosted")
    public ResponseEntity<Map<String, Boolean>> isProductBoosted(@PathVariable Long productId) {
        try {
            boolean isBoosted = boostService.isProductBoosted(productId);
            return ResponseEntity.ok(Map.of("isBoosted", isBoosted));
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du boost: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 