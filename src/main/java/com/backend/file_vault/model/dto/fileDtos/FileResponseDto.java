package com.backend.file_vault.model.dto.fileDtos;

import java.time.Instant;
import java.util.UUID;

public record FileResponseDto(
        UUID id,
        String originalFilename,
        Long size,
        String contentType,
        Instant createdAt
) {
}
