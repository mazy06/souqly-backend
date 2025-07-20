package io.mazy.souqly_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.mazy.souqly_backend.entity.ProductFormValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFormValueDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("productId")
    private Long productId;
    
    @JsonProperty("fieldId")
    private Long fieldId;
    
    @JsonProperty("fieldKey")
    private String fieldKey;
    
    @JsonProperty("fieldLabel")
    private String fieldLabel;
    
    @JsonProperty("fieldType")
    private String fieldType;
    
    @JsonProperty("fieldValue")
    private String fieldValue;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public ProductFormValueDTO(ProductFormValue value) {
        this.id = value.getId();
        this.productId = value.getProduct().getId();
        this.fieldId = value.getField().getId();
        this.fieldKey = value.getField().getFieldKey();
        this.fieldLabel = value.getField().getFieldLabel();
        this.fieldType = value.getField().getFieldType();
        this.fieldValue = value.getFieldValue();
        this.createdAt = value.getCreatedAt();
        this.updatedAt = value.getUpdatedAt();
    }
} 