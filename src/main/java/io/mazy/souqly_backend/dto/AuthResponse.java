package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserDto user;
    private String tokenType = "Bearer";
    private Long expiresIn;
    
    public AuthResponse(String token, String refreshToken, User user, Long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = new UserDto(user);
        this.expiresIn = expiresIn;
    }
} 