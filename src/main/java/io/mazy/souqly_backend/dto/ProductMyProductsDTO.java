package io.mazy.souqly_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.mazy.souqly_backend.entity.Product;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductMyProductsDTO {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("favoriteCount")
    private Integer favoriteCount;
    
    @JsonProperty("isBoosted")
    private Boolean isBoosted;
    
    @JsonProperty("boostExpiresAt")
    private LocalDateTime boostExpiresAt;
    
    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonProperty("images")
    private List<ProductImageDTO> images;
    
    @JsonProperty("category")
    private CategoryDTO category;
    
    @JsonProperty("seller")
    private UserDTO seller;
    
    // Constructeurs
    public ProductMyProductsDTO() {}
    
    public ProductMyProductsDTO(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = BigDecimal.valueOf(product.getPrice());
        this.status = product.getStatus().name();
        this.city = product.getCity();
        this.country = product.getCountry();
        this.state = null; // Pas de propriété state dans Product
        this.favoriteCount = product.getFavoriteCount();
        this.isBoosted = product.getIsBoosted();
        this.boostExpiresAt = null; // Pas de propriété boostExpiresAt dans Product
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        
        // Convertir les images
        if (product.getImages() != null) {
            this.images = product.getImages().stream()
                .map(ProductImageDTO::new)
                .toList();
        }
        
        // Convertir la catégorie
        if (product.getCategory() != null) {
            this.category = new CategoryDTO(product.getCategory());
        }
        
        // Convertir le vendeur
        if (product.getSeller() != null) {
            this.seller = new UserDTO(product.getSeller());
        }
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    
    public Boolean getIsBoosted() { return isBoosted; }
    public void setIsBoosted(Boolean isBoosted) { this.isBoosted = isBoosted; }
    
    public LocalDateTime getBoostExpiresAt() { return boostExpiresAt; }
    public void setBoostExpiresAt(LocalDateTime boostExpiresAt) { this.boostExpiresAt = boostExpiresAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<ProductImageDTO> getImages() { return images; }
    public void setImages(List<ProductImageDTO> images) { this.images = images; }
    
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
    
    public UserDTO getSeller() { return seller; }
    public void setSeller(UserDTO seller) { this.seller = seller; }
    
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public static class ProductImageDTO {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("fileName")
        private String fileName;
        
        @JsonProperty("contentType")
        private String contentType;
        
        @JsonProperty("content")
        private byte[] content;
        
        public ProductImageDTO(io.mazy.souqly_backend.entity.ProductImage image) {
            this.id = image.getId();
            this.fileName = image.getFileName();
            this.contentType = image.getContentType();
            this.content = null; // Pas de contenu pour éviter les problèmes de sérialisation
        }
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public static class CategoryDTO {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("key")
        private String key;
        
        @JsonProperty("label")
        private String label;
        
        @JsonProperty("iconName")
        private String iconName;
        
        @JsonProperty("badgeText")
        private String badgeText;
        
        @JsonProperty("active")
        private Boolean active;
        
        @JsonProperty("sortOrder")
        private Integer sortOrder;
        
        @JsonProperty("children")
        private List<CategoryDTO> children;
        
        public CategoryDTO(io.mazy.souqly_backend.entity.Category category) {
            this.id = category.getId();
            this.key = category.getKey();
            this.label = category.getLabel();
            this.iconName = category.getIconName();
            this.badgeText = category.getBadgeText();
            this.active = category.isActive();
            this.sortOrder = category.getSortOrder();
            // Mappe explicitement les enfants si présents
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                this.children = category.getChildren().stream()
                    .map(CategoryDTO::new)
                    .toList();
            } else {
                this.children = null;
            }
        }
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public static class UserDTO {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("firstName")
        private String firstName;
        
        @JsonProperty("lastName")
        private String lastName;
        
        @JsonProperty("phone")
        private String phone;
        
        public UserDTO(io.mazy.souqly_backend.entity.User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.phone = user.getPhone();
        }
    }
} 