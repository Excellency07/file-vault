package com.backend.file_vault.model.dto.accountDtos;

public record RefreshTokenResponseDto(
        String accessToken,
        String refreshToken
) {
}
