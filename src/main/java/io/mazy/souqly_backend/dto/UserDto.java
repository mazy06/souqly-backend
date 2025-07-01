package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private User.UserRole role;
    private User.AuthProvider authProvider;
    private String providerId;
    private boolean enabled;
    private boolean guest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor from entity
    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.profilePicture = user.getProfilePicture();
        this.role = user.getRole();
        this.authProvider = user.getAuthProvider();
        this.providerId = user.getProviderId();
        this.enabled = user.isEnabled();
        this.guest = user.isGuest();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
} 