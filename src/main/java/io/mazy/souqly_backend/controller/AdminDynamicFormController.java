package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.dto.CategoryDto;
import io.mazy.souqly_backend.dto.DynamicFormDTO;
import io.mazy.souqly_backend.entity.Category;
import io.mazy.souqly_backend.service.CategoryService;
import io.mazy.souqly_backend.service.DynamicFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/forms")
// @PreAuthorize("hasRole('ADMIN')") // Temporairement désactivé pour le développement
public class AdminDynamicFormController {
    
    @Autowired
    private DynamicFormService dynamicFormService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Récupère tous les formulaires actifs
     */
    @GetMapping
    public ResponseEntity<List<DynamicFormDTO>> getAllForms() {
        List<DynamicFormDTO> forms = dynamicFormService.getAllForms();
        return ResponseEntity.ok(forms);
    }
    
    /**
     * Récupère un formulaire par ID
     */
    @GetMapping("/{formId}")
    public ResponseEntity<DynamicFormDTO> getFormById(@PathVariable Long formId) {
        try {
            DynamicFormDTO form = dynamicFormService.getFormByIdWithFields(formId);
            return ResponseEntity.ok(form);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crée un nouveau formulaire
     */
    @PostMapping
    public ResponseEntity<DynamicFormDTO> createForm(@RequestBody DynamicFormDTO formDTO) {
        try {
            // Récupérer la catégorie
            Optional<CategoryDto> categoryDto = categoryService.getCategoryById(formDTO.getCategoryId());
            if (categoryDto.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Créer une entité Category à partir du DTO
            Category category = new Category();
            category.setId(categoryDto.get().getId());
            category.setKey(categoryDto.get().getKey());
            category.setLabel(categoryDto.get().getLabel());
            
            DynamicFormDTO createdForm = dynamicFormService.createForm(formDTO, category);
            return ResponseEntity.ok(createdForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Met à jour un formulaire existant
     */
    @PutMapping("/{formId}")
    public ResponseEntity<DynamicFormDTO> updateForm(
            @PathVariable Long formId,
            @RequestBody DynamicFormDTO formDTO) {
        try {
            DynamicFormDTO updatedForm = dynamicFormService.updateForm(formId, formDTO);
            return ResponseEntity.ok(updatedForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Supprime un formulaire
     */
    @DeleteMapping("/{formId}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long formId) {
        try {
            dynamicFormService.deleteForm(formId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Ajoute un champ à un formulaire
     */
    @PostMapping("/{formId}/fields")
    public ResponseEntity<DynamicFormDTO> addField(
            @PathVariable Long formId,
            @RequestBody DynamicFormDTO.FormFieldDTO fieldDTO) {
        try {
            DynamicFormDTO updatedForm = dynamicFormService.addFieldToForm(formId, fieldDTO);
            return ResponseEntity.ok(updatedForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Met à jour un champ d'un formulaire
     */
    @PutMapping("/{formId}/fields/{fieldId}")
    public ResponseEntity<DynamicFormDTO> updateField(
            @PathVariable Long formId,
            @PathVariable Long fieldId,
            @RequestBody DynamicFormDTO.FormFieldDTO fieldDTO) {
        try {
            DynamicFormDTO updatedForm = dynamicFormService.updateFieldInForm(formId, fieldId, fieldDTO);
            return ResponseEntity.ok(updatedForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Supprime un champ d'un formulaire
     */
    @DeleteMapping("/{formId}/fields/{fieldId}")
    public ResponseEntity<DynamicFormDTO> deleteField(
            @PathVariable Long formId,
            @PathVariable Long fieldId) {
        try {
            DynamicFormDTO updatedForm = dynamicFormService.deleteFieldFromForm(formId, fieldId);
            return ResponseEntity.ok(updatedForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Active/désactive un formulaire
     */
    @PatchMapping("/{formId}/toggle-status")
    public ResponseEntity<DynamicFormDTO> toggleFormStatus(@PathVariable Long formId) {
        try {
            DynamicFormDTO updatedForm = dynamicFormService.toggleFormStatus(formId);
            return ResponseEntity.ok(updatedForm);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 