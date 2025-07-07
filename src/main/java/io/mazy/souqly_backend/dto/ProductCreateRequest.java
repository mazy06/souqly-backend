package io.mazy.souqly_backend.dto;

import lombok.Data;

import java.util.List;

@Data
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
    public String city;
    public String country;

}