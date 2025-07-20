package io.mazy.souqly_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.mazy.souqly_backend.entity.DynamicForm;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("categoryId")
    private Long categoryId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("isActive")
    private Boolean isActive;
    
    @JsonProperty("fields")
    private List<FormFieldDTO> fields;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public DynamicFormDTO(DynamicForm form) {
        this.id = form.getId();
        this.categoryId = form.getCategory().getId();
        this.name = form.getName();
        this.description = form.getDescription();
        this.isActive = form.getIsActive();
        this.createdAt = form.getCreatedAt();
        this.updatedAt = form.getUpdatedAt();
        
        if (form.getFields() != null) {
            this.fields = form.getFields().stream()
                .map(FormFieldDTO::new)
                .collect(Collectors.toList());
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormFieldDTO {
        
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("fieldKey")
        private String fieldKey;
        
        @JsonProperty("fieldLabel")
        private String fieldLabel;
        
        @JsonProperty("fieldType")
        private String fieldType;
        
        @JsonProperty("fieldPlaceholder")
        private String fieldPlaceholder;
        
        @JsonProperty("fieldRequired")
        private Boolean fieldRequired;
        
        @JsonProperty("fieldOptions")
        private String fieldOptions;
        
        @JsonProperty("fieldValidation")
        private String fieldValidation;
        
        @JsonProperty("fieldOrder")
        private Integer fieldOrder;
        
        public FormFieldDTO(io.mazy.souqly_backend.entity.FormField field) {
            this.id = field.getId();
            this.fieldKey = field.getFieldKey();
            this.fieldLabel = field.getFieldLabel();
            this.fieldType = field.getFieldType();
            this.fieldPlaceholder = field.getFieldPlaceholder();
            this.fieldRequired = field.getFieldRequired();
            this.fieldOptions = field.getFieldOptions();
            this.fieldValidation = field.getFieldValidation();
            this.fieldOrder = field.getFieldOrder();
        }
    }
} 