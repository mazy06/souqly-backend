package io.mazy.souqly_backend.dto;

import io.mazy.souqly_backend.entity.Address;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDto {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    public AddressDto(Address address) {
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.zipCode = address.getZipCode();
        this.country = address.getCountry();
    }
}
