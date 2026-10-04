package com.backend.file_vault.model.dto.accountDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank(message = "Old password is required")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must contains at least 8 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$",
                message = "Password must contain at least one uppercase and one lowercase letters, one number and one symbol")
        String newPassword,

        @NotBlank(message = "Confirmation password is required")
        String confirmPassword
) {
    public ResetPasswordRequestDto {
        oldPassword = (oldPassword != null) ? oldPassword.trim() : null;
        newPassword = (newPassword != null) ? newPassword.trim() : null;
        confirmPassword = (confirmPassword != null) ? confirmPassword.trim() : null;
    }
}
