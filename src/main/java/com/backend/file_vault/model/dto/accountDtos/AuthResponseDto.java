package com.backend.file_vault.model.dto.accountDtos;

public record AuthResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        String email,
        long expiresIn
) {
    public AuthResponseDto(String accessToken, String refreshToken, String email, long expiresIn) {
        this(accessToken, refreshToken, "bearer", email, expiresIn);
    }
}
