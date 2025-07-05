package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.ProductImage;
import java.util.List;

public class ProductListDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String brand;
    private String size;
    private String condition;
    private String shippingInfo;
    private String status;
    private int favoriteCount;
    private List<ProductImage> images;
    // Ajoute d'autres champs si besoin

    public ProductListDTO(Product product, int favoriteCount) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.brand = product.getBrand();
        this.size = product.getSize();
        this.condition = product.getCondition();
        this.shippingInfo = product.getShippingInfo();
        this.status = product.getStatus().toString();
        this.favoriteCount = favoriteCount;
        this.images = product.getImages();
    }

    // Getters et setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public String getBrand() { return brand; }
    public String getSize() { return size; }
    public String getCondition() { return condition; }
    public String getShippingInfo() { return shippingInfo; }
    public String getStatus() { return status; }
    public int getFavoriteCount() { return favoriteCount; }
    public List<ProductImage> getImages() { return images; }
} 