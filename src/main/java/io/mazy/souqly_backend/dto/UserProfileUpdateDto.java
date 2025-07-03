package io.mazy.souqly_backend.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDto {
    private String firstName;
    private String lastName;
    private String phone;
    private AddressDto address;
}
