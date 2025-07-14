package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.FavoriteResponse;
import io.mazy.souqly_backend.dto.ProductListDTO;
import io.mazy.souqly_backend.entity.Favorite;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.service.FavoriteService;
import io.mazy.souqly_backend.service.ProductService;
import io.mazy.souqly_backend.service.UserService;
import io.mazy.souqly_backend.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    /**
     * Toggle favori pour un produit
     * POST /api/products/{productId}/favorite
     */
    @PostMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteResponse> toggleFavorite(
            @PathVariable Long productId,
            Authentication authentication) {
        
        // Récupérer l'ID de l'utilisateur depuis l'authentification
        Long userId = getUserIdFromAuthentication(authentication);
        
        FavoriteResponse response = favoriteService.toggleFavorite(productId, userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Vérifier si un produit est en favori
     * GET /api/products/{productId}/favorite
     */
    @GetMapping("/{productId}/favorite")
    public ResponseEntity<FavoriteResponse> checkFavorite(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isFavorite = favoriteService.isFavorite(productId, userId);
        int favoriteCount = favoriteService.getFavoriteCount(productId);
        
        FavoriteResponse response = new FavoriteResponse(isFavorite, favoriteCount);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Récupérer les produits favoris de l'utilisateur
     * GET /api/products/favorites
     */
    @GetMapping("/favorites")
    public ResponseEntity<List<ProductListDTO>> getUserFavorites(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<Favorite> userFavorites = favoriteService.getUserFavorites(userId);
        List<ProductListDTO> favoriteProducts = userFavorites.stream()
            .map(favorite -> {
                Product product = productService.getProduct(favorite.getProductId()).orElse(null);
                if (product != null) {
                    product.setImages(productImageRepository.findByProductId(product.getId()));
                    return new ProductListDTO(product, favoriteService.getFavoriteCount(product.getId()));
                }
                return null;
            })
            .filter(dto -> dto != null)
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(favoriteProducts);
    }
    
    /**
     * Récupérer le compteur de favoris d'un produit
     * GET /api/products/{productId}/favorites/count
     */
    @GetMapping("/{productId}/favorites/count")
    public ResponseEntity<FavoriteResponse> getFavoriteCount(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long userId = getUserIdFromAuthentication(authentication);
        boolean isFavorite = favoriteService.isFavorite(productId, userId);
        int favoriteCount = favoriteService.getFavoriteCount(productId);
        
        FavoriteResponse response = new FavoriteResponse(isFavorite, favoriteCount);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Méthode utilitaire pour récupérer l'ID utilisateur depuis l'authentification
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        return userService.getCurrentUserId();
    }
} 