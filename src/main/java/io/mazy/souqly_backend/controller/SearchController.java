package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.entity.elasticsearch.ProductDocument;
import io.mazy.souqly_backend.service.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> result = elasticsearchService.searchProducts(query, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber());
        response.put("size", result.getSize());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductDocument>> getSuggestions(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(0, size);
        List<ProductDocument> suggestions = elasticsearchService.findSuggestions(query, pageable);
        
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/sync")
    public ResponseEntity<String> syncProducts() {
        try {
            elasticsearchService.syncAllProducts();
            return ResponseEntity.ok("Synchronisation terminée avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la synchronisation: " + e.getMessage());
        }
    }
} 