package io.mazy.souqly_backend.dto;

public class UserActionRequest {
    private String action;
    private String reason;

    // Constructeurs
    public UserActionRequest() {}

    public UserActionRequest(String action, String reason) {
        this.action = action;
        this.reason = reason;
    }

    // Getters et Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 