package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.Product;
import io.mazy.souqly_backend.entity.ProductImage;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<ImageMeta> images;
    private Boolean isBoosted;
    private Integer boostLevel;
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
        this.isBoosted = product.getIsBoosted() != null ? product.getIsBoosted() : false;
        this.boostLevel = product.getBoostLevel() != null ? product.getBoostLevel() : 0;
        this.images = product.getImages().stream()
            .map(img -> new ImageMeta(img.getId(), img.getFileName(), img.getContentType()))
            .collect(Collectors.toList());
    }

    public static class ImageMeta {
        private Long id;
        private String fileName;
        private String contentType;
        public ImageMeta(Long id, String fileName, String contentType) {
            this.id = id;
            this.fileName = fileName;
            this.contentType = contentType;
        }
        public Long getId() { return id; }
        public String getFileName() { return fileName; }
        public String getContentType() { return contentType; }
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
    public List<ImageMeta> getImages() { return images; }
    public Boolean getIsBoosted() { return isBoosted; }
    public Integer getBoostLevel() { return boostLevel; }
} 