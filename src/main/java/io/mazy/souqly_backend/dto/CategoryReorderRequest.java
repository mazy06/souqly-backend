package io.mazy.souqly_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryReorderRequest {
    private Long id;
    private Integer sortOrder;
    private Long parentId;
} 