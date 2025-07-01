package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.ProductCreateRequest;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> createProduct(
        @RequestBody ProductCreateRequest req,
        @AuthenticationPrincipal User user
    ) {
        Product product = productService.createProduct(req, user.getId());
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) String condition,
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) String size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortOrder
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage = productService.getProductsForListing(pageable, categoryId, minPrice, 
            maxPrice, condition, brand, size, search, sortBy, sortOrder);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", productPage.getNumber());
        response.put("size", productPage.getSize());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProduct(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> updateProduct(
        @PathVariable Long id,
        @RequestBody ProductCreateRequest req,
        @AuthenticationPrincipal User user
    ) {
        try {
            Product product = productService.updateProduct(id, req, user.getId());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long id,
        @AuthenticationPrincipal User user
    ) {
        try {
            productService.deleteProduct(id, user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
        @PathVariable Long id,
        @AuthenticationPrincipal User user
    ) {
        try {
            productService.toggleFavorite(id, user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("isFavorite", true);
            response.put("favoriteCount", 1); // TODO: Récupérer le vrai compteur
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getFavorites(@AuthenticationPrincipal User user) {
        List<Product> favorites = productService.getFavoriteProducts(user.getId());
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/my-products")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getMyProducts(@AuthenticationPrincipal User user) {
        List<Product> products = productService.getProductsBySeller(user.getId());
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> toggleProductStatus(
        @PathVariable Long id,
        @AuthenticationPrincipal User user
    ) {
        try {
            Product product = productService.toggleProductStatus(id, user.getId());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 