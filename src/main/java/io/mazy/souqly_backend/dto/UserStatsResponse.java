package io.mazy.souqly_backend.dto;

import java.util.Map;

public class UserStatsResponse {
    private Long total;
    private Long active;
    private Long suspended;
    private Long banned;
    private Map<String, Long> byRole;

    // Constructeurs
    public UserStatsResponse() {}

    public UserStatsResponse(Long total, Long active, Long suspended, Long banned, Map<String, Long> byRole) {
        this.total = total;
        this.active = active;
        this.suspended = suspended;
        this.banned = banned;
        this.byRole = byRole;
    }

    // Getters et Setters
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getActive() {
        return active;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public Long getSuspended() {
        return suspended;
    }

    public void setSuspended(Long suspended) {
        this.suspended = suspended;
    }

    public Long getBanned() {
        return banned;
    }

    public void setBanned(Long banned) {
        this.banned = banned;
    }

    public Map<String, Long> getByRole() {
        return byRole;
    }

    public void setByRole(Map<String, Long> byRole) {
        this.byRole = byRole;
    }
} 