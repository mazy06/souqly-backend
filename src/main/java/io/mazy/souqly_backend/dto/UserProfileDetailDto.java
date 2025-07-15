package io.mazy.souqly_backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDetailDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePictureUrl;
    private AddressDto address;
    private LocalDateTime createdAt;
    private long followersCount;
    private long followingCount;
    private long productsCount;
    private boolean isFollowing;
    private boolean isOwnProfile;
} 