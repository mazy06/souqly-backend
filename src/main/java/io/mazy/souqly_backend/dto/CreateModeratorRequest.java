package io.mazy.souqly_backend.dto;

public class CreateModeratorRequest {
    private String email;
    private String reason;

    // Constructeurs
    public CreateModeratorRequest() {}

    public CreateModeratorRequest(String email, String reason) {
        this.email = email;
        this.reason = reason;
    }

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 