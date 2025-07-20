package io.mazy.souqly_backend.dto;

import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Integer productsCount;
    private Double rating;

    // Constructeurs
    public UserDto() {}

    public UserDto(Long id, String firstName, String lastName, String email, String role, String status, 
                   LocalDateTime createdAt, LocalDateTime lastLoginAt, Integer productsCount, Double rating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.productsCount = productsCount;
        this.rating = rating;
    }

    // Constructeur pour compatibilité avec l'ancien code
    public UserDto(io.mazy.souqly_backend.entity.User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.status = user.isBanned() ? "BANNED" : (user.isEnabled() ? "ACTIVE" : "SUSPENDED");
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
        this.productsCount = 0; // Sera calculé séparément
        this.rating = 5.0; // Hardcodé pour l'instant
    }

    // Constructeur pour compatibilité avec l'ancien code
    public UserDto(io.mazy.souqly_backend.entity.User user, int adsCount) {
        this(user);
        this.productsCount = adsCount;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Integer getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(Integer productsCount) {
        this.productsCount = productsCount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
} 