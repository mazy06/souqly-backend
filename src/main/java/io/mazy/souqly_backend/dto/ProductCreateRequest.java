package io.mazy.souqly_backend.dto;

import java.util.List;

public class ProductCreateRequest {
    public String title;
    public String description;
    public Double price;
    public String brand;
    public String size;
    public String condition;
    public String shippingInfo;
    public List<Long> imageIds;
    public Long categoryId;
} 