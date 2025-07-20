package io.mazy.souqly_backend.service;

import io.mazy.souqly_backend.dto.DynamicFormDTO;
import io.mazy.souqly_backend.dto.ProductFormValueDTO;
import io.mazy.souqly_backend.entity.*;
import io.mazy.souqly_backend.repository.DynamicFormRepository;
import io.mazy.souqly_backend.repository.FormFieldRepository;
import io.mazy.souqly_backend.repository.ProductFormValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DynamicFormService {
    
    @Autowired
    private DynamicFormRepository dynamicFormRepository;
    
    @Autowired
    private FormFieldRepository formFieldRepository;
    
    @Autowired
    private ProductFormValueRepository productFormValueRepository;
    
    /**
     * Récupère le formulaire dynamique pour une catégorie donnée
     */
    public Optional<DynamicFormDTO> getFormByCategoryId(Long categoryId) {
        return dynamicFormRepository.findByCategoryIdAndActive(categoryId)
                .map(DynamicFormDTO::new);
    }
    
    /**
     * Récupère tous les formulaires (actifs et inactifs)
     */
    public List<DynamicFormDTO> getAllForms() {
        return dynamicFormRepository.findAllForms().stream()
                .map(DynamicFormDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les formulaires actifs
     */
    public List<DynamicFormDTO> getAllActiveForms() {
        return dynamicFormRepository.findAllActive().stream()
                .map(DynamicFormDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un formulaire par ID
     */
    public Optional<DynamicFormDTO> getFormById(Long formId) {
        return dynamicFormRepository.findById(formId)
                .map(DynamicFormDTO::new);
    }
    
    /**
     * Récupère un formulaire par ID avec tous les champs
     */
    public DynamicFormDTO getFormByIdWithFields(Long formId) {
        Optional<DynamicForm> form = dynamicFormRepository.findById(formId);
        if (form.isPresent()) {
            return new DynamicFormDTO(form.get());
        }
        throw new RuntimeException("Formulaire non trouvé");
    }
    
    /**
     * Crée un nouveau formulaire dynamique
     */
    public DynamicFormDTO createForm(DynamicFormDTO formDTO, Category category) {
        DynamicForm form = new DynamicForm();
        form.setCategory(category);
        form.setName(formDTO.getName());
        form.setDescription(formDTO.getDescription());
        form.setIsActive(true);
        
        DynamicForm savedForm = dynamicFormRepository.save(form);
        
        // Sauvegarder les champs du formulaire
        if (formDTO.getFields() != null) {
            for (DynamicFormDTO.FormFieldDTO fieldDTO : formDTO.getFields()) {
                FormField field = new FormField();
                field.setForm(savedForm);
                field.setFieldKey(fieldDTO.getFieldKey());
                field.setFieldLabel(fieldDTO.getFieldLabel());
                field.setFieldType(fieldDTO.getFieldType());
                field.setFieldPlaceholder(fieldDTO.getFieldPlaceholder());
                field.setFieldRequired(fieldDTO.getFieldRequired());
                field.setFieldOptions(fieldDTO.getFieldOptions());
                field.setFieldValidation(fieldDTO.getFieldValidation());
                field.setFieldOrder(fieldDTO.getFieldOrder());
                
                formFieldRepository.save(field);
            }
        }
        
        return new DynamicFormDTO(savedForm);
    }
    
    /**
     * Met à jour un formulaire existant
     */
    public DynamicFormDTO updateForm(Long formId, DynamicFormDTO formDTO) {
        Optional<DynamicForm> existingForm = dynamicFormRepository.findById(formId);
        if (existingForm.isEmpty()) {
            throw new RuntimeException("Formulaire non trouvé");
        }
        
        DynamicForm form = existingForm.get();
        form.setName(formDTO.getName());
        form.setDescription(formDTO.getDescription());
        form.setIsActive(formDTO.getIsActive());
        
        DynamicForm savedForm = dynamicFormRepository.save(form);
        
        // Supprimer les anciens champs
        List<FormField> existingFields = formFieldRepository.findByFormIdOrderByFieldOrder(formId);
        formFieldRepository.deleteAll(existingFields);
        
        // Ajouter les nouveaux champs
        if (formDTO.getFields() != null) {
            for (DynamicFormDTO.FormFieldDTO fieldDTO : formDTO.getFields()) {
                FormField field = new FormField();
                field.setForm(savedForm);
                field.setFieldKey(fieldDTO.getFieldKey());
                field.setFieldLabel(fieldDTO.getFieldLabel());
                field.setFieldType(fieldDTO.getFieldType());
                field.setFieldPlaceholder(fieldDTO.getFieldPlaceholder());
                field.setFieldRequired(fieldDTO.getFieldRequired());
                field.setFieldOptions(fieldDTO.getFieldOptions());
                field.setFieldValidation(fieldDTO.getFieldValidation());
                field.setFieldOrder(fieldDTO.getFieldOrder());
                
                formFieldRepository.save(field);
            }
        }
        
        return new DynamicFormDTO(savedForm);
    }
    
    /**
     * Sauvegarde les valeurs de formulaire pour un produit
     */
    public void saveProductFormValues(Long productId, Map<String, String> formValues) {
        // Supprimer les anciennes valeurs
        List<ProductFormValue> existingValues = productFormValueRepository.findByProductId(productId);
        productFormValueRepository.deleteAll(existingValues);
        
        // Sauvegarder les nouvelles valeurs
        for (Map.Entry<String, String> entry : formValues.entrySet()) {
            String fieldKey = entry.getKey();
            String fieldValue = entry.getValue();
            
            // Trouver le champ correspondant
            Optional<FormField> field = formFieldRepository.findAll().stream()
                    .filter(f -> f.getFieldKey().equals(fieldKey))
                    .findFirst();
            
            if (field.isPresent()) {
                ProductFormValue value = new ProductFormValue();
                value.setField(field.get());
                value.setFieldValue(fieldValue);
                
                // Créer un produit temporaire pour la relation
                Product product = new Product();
                product.setId(productId);
                value.setProduct(product);
                
                productFormValueRepository.save(value);
            }
        }
    }
    
    /**
     * Récupère les valeurs de formulaire pour un produit
     */
    public List<ProductFormValueDTO> getProductFormValues(Long productId) {
        return productFormValueRepository.findByProductId(productId).stream()
                .map(ProductFormValueDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Supprime les valeurs de formulaire pour un produit
     */
    public void deleteProductFormValues(Long productId) {
        List<ProductFormValue> values = productFormValueRepository.findByProductId(productId);
        productFormValueRepository.deleteAll(values);
    }
    
    /**
     * Supprime un formulaire
     */
    public void deleteForm(Long formId) {
        Optional<DynamicForm> form = dynamicFormRepository.findById(formId);
        if (form.isPresent()) {
            // Supprimer d'abord les champs du formulaire
            List<FormField> fields = formFieldRepository.findByFormIdOrderByFieldOrder(formId);
            
            // Supprimer les valeurs associées à chaque champ
            for (FormField field : fields) {
                List<ProductFormValue> values = productFormValueRepository.findByFieldId(field.getId());
                productFormValueRepository.deleteAll(values);
            }
            
            // Supprimer les champs du formulaire
            formFieldRepository.deleteAll(fields);
            
            // Supprimer le formulaire
            dynamicFormRepository.deleteById(formId);
        }
    }
    
    /**
     * Ajoute un champ à un formulaire
     */
    public DynamicFormDTO addFieldToForm(Long formId, DynamicFormDTO.FormFieldDTO fieldDTO) {
        Optional<DynamicForm> form = dynamicFormRepository.findById(formId);
        if (form.isEmpty()) {
            throw new RuntimeException("Formulaire non trouvé");
        }
        
        FormField field = new FormField();
        field.setForm(form.get());
        field.setFieldKey(fieldDTO.getFieldKey());
        field.setFieldLabel(fieldDTO.getFieldLabel());
        field.setFieldType(fieldDTO.getFieldType());
        field.setFieldPlaceholder(fieldDTO.getFieldPlaceholder());
        field.setFieldRequired(fieldDTO.getFieldRequired());
        field.setFieldOptions(fieldDTO.getFieldOptions());
        field.setFieldValidation(fieldDTO.getFieldValidation());
        field.setFieldOrder(fieldDTO.getFieldOrder());
        
        formFieldRepository.save(field);
        
        return new DynamicFormDTO(form.get());
    }
    
    /**
     * Met à jour un champ d'un formulaire
     */
    public DynamicFormDTO updateFieldInForm(Long formId, Long fieldId, DynamicFormDTO.FormFieldDTO fieldDTO) {
        Optional<DynamicForm> form = dynamicFormRepository.findById(formId);
        if (form.isEmpty()) {
            throw new RuntimeException("Formulaire non trouvé");
        }
        
        Optional<FormField> existingField = formFieldRepository.findById(fieldId);
        if (existingField.isEmpty()) {
            throw new RuntimeException("Champ non trouvé");
        }
        
        FormField field = existingField.get();
        field.setFieldKey(fieldDTO.getFieldKey());
        field.setFieldLabel(fieldDTO.getFieldLabel());
        field.setFieldType(fieldDTO.getFieldType());
        field.setFieldPlaceholder(fieldDTO.getFieldPlaceholder());
        field.setFieldRequired(fieldDTO.getFieldRequired());
        field.setFieldOptions(fieldDTO.getFieldOptions());
        field.setFieldValidation(fieldDTO.getFieldValidation());
        field.setFieldOrder(fieldDTO.getFieldOrder());
        
        formFieldRepository.save(field);
        
        return new DynamicFormDTO(form.get());
    }
    
    /**
     * Supprime un champ d'un formulaire
     */
    public DynamicFormDTO deleteFieldFromForm(Long formId, Long fieldId) {
        Optional<DynamicForm> form = dynamicFormRepository.findById(formId);
        if (form.isEmpty()) {
            throw new RuntimeException("Formulaire non trouvé");
        }
        
        Optional<FormField> field = formFieldRepository.findById(fieldId);
        if (field.isPresent()) {
            // Supprimer les valeurs associées au champ
            List<ProductFormValue> values = productFormValueRepository.findByFieldId(fieldId);
            productFormValueRepository.deleteAll(values);
            
            // Supprimer le champ
            formFieldRepository.deleteById(fieldId);
        }
        
        return new DynamicFormDTO(form.get());
    }
    
    /**
     * Active/désactive un formulaire
     */
    public DynamicFormDTO toggleFormStatus(Long formId) {
        Optional<DynamicForm> form = dynamicFormRepository.findById(formId);
        if (form.isEmpty()) {
            throw new RuntimeException("Formulaire non trouvé");
        }
        
        DynamicForm dynamicForm = form.get();
        dynamicForm.setIsActive(!dynamicForm.getIsActive());
        
        DynamicForm savedForm = dynamicFormRepository.save(dynamicForm);
        return new DynamicFormDTO(savedForm);
    }
} 