package io.mazy.souqly_backend.dto;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String currentPassword;
    private String newPassword;
}
