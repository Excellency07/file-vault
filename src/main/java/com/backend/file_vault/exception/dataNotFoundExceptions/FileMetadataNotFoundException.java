package com.backend.file_vault.exception.dataNotFoundExceptions;

public class FileMetadataNotFoundException extends RuntimeException {
    public FileMetadataNotFoundException(String message) {
        super(message);
    }
}
