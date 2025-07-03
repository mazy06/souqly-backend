package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePictureUrl;
    private AddressDto address;
    
    public UserProfileDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.profilePictureUrl = user.getProfilePictureUrl();
        
        if (user.getAddress() != null) {
            this.address = new AddressDto(user.getAddress());
        }
    }
}
