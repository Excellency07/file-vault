package com.backend.file_vault.model.dto.accountDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterAccountRequestDto(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name field must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must contains at least 8 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$",
                message = "Password must contain at least one uppercase and one lowercase letters, one number and one symbol")
        String password
) {

    public RegisterAccountRequestDto {
        name = (name != null) ? name.trim() : null;
        email = (email != null) ? email.trim() : null;
        password = (password != null) ? password.trim() : null;
    }
}
