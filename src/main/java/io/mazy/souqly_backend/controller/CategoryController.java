package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.CategoryDto;
import io.mazy.souqly_backend.dto.CategoryReorderRequest;
import io.mazy.souqly_backend.entity.Category;
import io.mazy.souqly_backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Categories", description = "Endpoints pour la gestion des catégories")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    @Operation(summary = "Liste toutes les catégories", description = "Récupère la liste complète des catégories")
    @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/tree")
    @Operation(summary = "Arborescence des catégories", description = "Récupère l'arborescence complète des catégories")
    @ApiResponse(responseCode = "200", description = "Arborescence des catégories récupérée")
    public ResponseEntity<List<CategoryDto>> getCategoryTree() {
        List<CategoryDto> tree = categoryService.getCategoryTree();
        return ResponseEntity.ok(tree);
    }
    
    @GetMapping("/tree/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Arborescence complète des catégories", description = "Récupère l'arborescence complète des catégories (actives et inactives) - Admin uniquement")
    @ApiResponse(responseCode = "200", description = "Arborescence complète des catégories récupérée")
    public ResponseEntity<List<CategoryDto>> getAllCategoryTree() {
        List<CategoryDto> tree = categoryService.getAllCategoryTree();
        return ResponseEntity.ok(tree);
    }
    
    @GetMapping("/root")
    @Operation(summary = "Catégories racines", description = "Récupère uniquement les catégories racines")
    @ApiResponse(responseCode = "200", description = "Catégories racines récupérées")
    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        List<CategoryDto> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(rootCategories);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Catégorie par ID", description = "Récupère une catégorie par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie trouvée",
                content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/key/{key}")
    @Operation(summary = "Catégorie par clé", description = "Récupère une catégorie par sa clé")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie trouvée",
                content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CategoryDto> getCategoryByKey(@PathVariable String key) {
        return categoryService.getCategoryByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/children")
    @Operation(summary = "Sous-catégories", description = "Récupère les sous-catégories d'une catégorie")
    @ApiResponse(responseCode = "200", description = "Sous-catégories récupérées")
    public ResponseEntity<List<CategoryDto>> getChildrenCategories(@PathVariable Long id) {
        List<CategoryDto> children = categoryService.getChildrenCategories(id);
        return ResponseEntity.ok(children);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une catégorie", description = "Crée une nouvelle catégorie (Admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie créée",
                content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<CategoryDto> createCategory(@RequestBody Category category) {
        CategoryDto createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une catégorie", description = "Modifie une catégorie existante (Admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie modifiée",
                content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        return categoryService.updateCategory(id, categoryDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une catégorie", description = "Supprime une catégorie (Admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Catégorie supprimée"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée"),
        @ApiResponse(responseCode = "400", description = "Impossible de supprimer une catégorie avec des enfants")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            boolean deleted = categoryService.deleteCategory(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Rechercher des catégories", description = "Recherche des catégories par terme")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<CategoryDto>> searchCategories(@RequestParam String q) {
        List<CategoryDto> results = categoryService.searchCategories(q);
        return ResponseEntity.ok(results);
    }
    
    @PutMapping("/{id}/move")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Déplacer une catégorie", description = "Déplace une catégorie vers un nouveau parent (Admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catégorie déplacée",
                content = @Content(schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<CategoryDto> moveCategory(
            @PathVariable Long id,
            @RequestParam(required = false) Long newParentId) {
        try {
            CategoryDto movedCategory = categoryService.moveCategory(id, newParentId);
            return ResponseEntity.ok(movedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/exists/{key}")
    @Operation(summary = "Vérifier l'existence d'une clé", description = "Vérifie si une clé de catégorie existe")
    @ApiResponse(responseCode = "200", description = "Résultat de la vérification")
    public ResponseEntity<Boolean> existsByKey(@PathVariable String key) {
        boolean exists = categoryService.existsByKey(key);
        return ResponseEntity.ok(exists);
    }
    
    @PutMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Réorganiser l'ordre des catégories", description = "Met à jour l'ordre de plusieurs catégories (Admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ordre mis à jour"),
        @ApiResponse(responseCode = "403", description = "Accès refusé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<List<CategoryDto>> reorderCategories(@RequestBody List<CategoryReorderRequest> reorderRequests) {
        try {
            List<CategoryDto> updatedCategories = categoryService.reorderCategories(reorderRequests);
            return ResponseEntity.ok(updatedCategories);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 