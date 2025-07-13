package io.mazy.souqly_backend.dto;

public class ProductCreateRequest {
    private String city;
    private String country;

    public ProductCreateRequest() {}

    public ProductCreateRequest(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
} 