package com.backend.file_vault.model.dto.accountDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdateAccountDetailsRequestDto(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name field must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email
        String email
) {
    public UpdateAccountDetailsRequestDto {
        name = (name != null) ? name.trim() : null;
        email = (email != null) ? email.trim() : null;
    }
}
