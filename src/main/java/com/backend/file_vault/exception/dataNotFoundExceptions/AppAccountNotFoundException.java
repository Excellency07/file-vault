package com.backend.file_vault.exception.dataNotFoundExceptions;

public class AppAccountNotFoundException extends RuntimeException {
    public AppAccountNotFoundException(String message) {
        super(message);
    }
}
