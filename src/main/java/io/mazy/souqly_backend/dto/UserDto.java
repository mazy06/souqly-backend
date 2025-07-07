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
    private String phone;
    private String profilePicture;
    private String role;
    private User.AuthProvider authProvider;
    private String providerId;
    private boolean enabled;
    private boolean guest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AddressDto address;
    private int adsCount; // nombre d'annonces publiées
    private double rating; // note moyenne (hardcodée)
    
    // Constructor from entity
    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.profilePicture = user.getProfilePictureUrl();
        this.role = user.getRole().name();
        this.authProvider = user.getAuthProvider();
        this.providerId = user.getProviderId();
        this.enabled = user.isEnabled();
        this.guest = user.isGuest();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        
        if (user.getAddress() != null) {
            this.address = new AddressDto(user.getAddress());
        }
    }

    public UserDto(User user, int adsCount) {
        this(user);
        this.adsCount = adsCount;
        this.rating = 5.0; // hardcodé pour l'instant
    }
} 