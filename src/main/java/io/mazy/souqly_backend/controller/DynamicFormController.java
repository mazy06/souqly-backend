package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.DynamicFormDTO;
import io.mazy.souqly_backend.dto.ProductFormValueDTO;
import io.mazy.souqly_backend.service.DynamicFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/forms")
public class DynamicFormController {
    
    @Autowired
    private DynamicFormService dynamicFormService;
    
    /**
     * Récupère le formulaire dynamique pour une catégorie
     */
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DynamicFormDTO> getFormByCategory(@PathVariable Long categoryId) {
        Optional<DynamicFormDTO> form = dynamicFormService.getFormByCategoryId(categoryId);
        return form.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Récupère les valeurs de formulaire pour un produit
     */
    @GetMapping("/product/{productId}/values")
    public ResponseEntity<List<ProductFormValueDTO>> getProductFormValues(@PathVariable Long productId) {
        List<ProductFormValueDTO> values = dynamicFormService.getProductFormValues(productId);
        return ResponseEntity.ok(values);
    }
    
    /**
     * Sauvegarde les valeurs de formulaire pour un produit
     */
    @PostMapping("/product/{productId}/values")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> saveProductFormValues(
            @PathVariable Long productId,
            @RequestBody Map<String, String> formValues) {
        try {
            dynamicFormService.saveProductFormValues(productId, formValues);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 