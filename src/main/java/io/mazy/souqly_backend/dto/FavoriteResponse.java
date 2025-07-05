package io.mazy.souqly_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FavoriteResponse {
    
    @JsonProperty("isFavorite")
    private boolean isFavorite;
    
    @JsonProperty("favoriteCount")
    private int favoriteCount;
    
    // Constructeurs
    public FavoriteResponse() {}
    
    public FavoriteResponse(boolean isFavorite, int favoriteCount) {
        this.isFavorite = isFavorite;
        this.favoriteCount = favoriteCount;
    }
    
    // Getters et Setters
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    public int getFavoriteCount() {
        return favoriteCount;
    }
    
    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
} 