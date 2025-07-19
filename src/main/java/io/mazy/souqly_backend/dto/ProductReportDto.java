package io.mazy.souqly_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ProductReportDto {
    private Long id;
    private Long productId;
    private Long userId;
    private List<String> reasons;
    private String customReason;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductDto product;
    private UserDto reporter;

    // Constructeurs
    public ProductReportDto() {}

    public ProductReportDto(Long id, Long productId, Long userId, List<String> reasons, 
                          String customReason, String description, String status, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.reasons = reasons;
        this.customReason = customReason;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public UserDto getReporter() {
        return reporter;
    }

    public void setReporter(UserDto reporter) {
        this.reporter = reporter;
    }

    // Classe interne pour les données du produit
    public static class ProductDto {
        private Long id;
        private String title;
        private String description;
        private Double price;
        private UserDto seller;

        public ProductDto() {}

        public ProductDto(Long id, String title, String description, Double price) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.price = price;
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public UserDto getSeller() {
            return seller;
        }

        public void setSeller(UserDto seller) {
            this.seller = seller;
        }
    }

    // Classe interne pour les données de l'utilisateur
    public static class UserDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;

        public UserDto() {}

        public UserDto(Long id, String firstName, String lastName, String email) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
} 