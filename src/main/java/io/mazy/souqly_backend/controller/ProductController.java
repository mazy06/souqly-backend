package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.ProductCreateRequest;
import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.User;
import io.mazy.souqly_backend.service.ProductService;
import io.mazy.souqly_backend.dto.ProductListDTO;
import io.mazy.souqly_backend.service.FavoriteService;
import io.mazy.souqly_backend.repository.ProductImageRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ProductImageRepository productImageRepository;

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
        
        List<ProductListDTO> productDTOs = productPage.getContent().stream()
            .map(product -> {
                product.setImages(productImageRepository.findByProductId(product.getId()));
                return new ProductListDTO(product, favoriteService.getFavoriteCount(product.getId()));
            })
            .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", productDTOs);
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", productPage.getNumber());
        response.put("size", productPage.getSize());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cacheable")
    public ResponseEntity<Map<String, Object>> getProductsCacheable(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        var productPage = productService.getProductsForListingCacheable(pageable);
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

    @GetMapping("/my-products")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getMyProducts(@AuthenticationPrincipal User user) {
        List<Product> products = productService.getProductsBySeller(user.getId());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/my-products/filtered")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Product>> getMyProductsFiltered(
        @AuthenticationPrincipal User user,
        @RequestParam(required = false) String status
    ) {
        System.out.println("[ProductController] getMyProductsFiltered appelé pour user: " + user.getId() + ", status: " + status);
        List<Product> products = productService.getProductsBySellerAndStatus(user.getId(), status);
        System.out.println("[ProductController] Nombre de produits trouvés: " + products.size());
        
        // Log des statuts des produits trouvés
        products.forEach(p -> System.out.println("[ProductController] Produit " + p.getId() + " - Status: " + p.getStatus()));
        
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

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> updateProductStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> request,
        @AuthenticationPrincipal User user
    ) {
        try {
            String status = request.get("status");
            if (status == null || (!status.equals("ACTIVE") && !status.equals("INACTIVE"))) {
                return ResponseEntity.badRequest().build();
            }
            Product product = productService.updateProductStatus(id, status, user.getId());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Récupérer les données de localisation d'un produit
    @GetMapping("/{id}/location")
    public ResponseEntity<Map<String, Object>> getProductLocation(@PathVariable Long id) {
        try {
            Optional<Product> productOpt = productService.getProduct(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Product product = productOpt.get();
            Map<String, Object> locationData = new HashMap<>();
            locationData.put("city", product.getCity());
            locationData.put("country", product.getCountry());
            
            return ResponseEntity.ok(locationData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/favorite-counts")
    public ResponseEntity<Map<Long, Long>> getFavoriteCounts(@RequestBody List<Long> productIds) {
        Map<Long, Long> counts = favoriteService.getFavoriteCountsForProducts(productIds);
        return ResponseEntity.ok(counts);
    }

    @PostMapping("/{id}/increment-view")
    public ResponseEntity<Void> incrementProductView(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            productService.incrementViewCount(id, user.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/mark-as-sold")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Product> markProductAsSold(
        @PathVariable Long id,
        @AuthenticationPrincipal User user
    ) {
        try {
            Product product = productService.markAsSold(id, user.getId());
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> getProductsBySeller(@PathVariable Long sellerId) {
        try {
            List<Product> products = productService.getProductsBySeller(sellerId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


} 