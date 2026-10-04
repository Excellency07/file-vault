package com.backend.file_vault.exception.invalidRequestExceptions;

public class PasswordConfirmationException extends RuntimeException {
    public PasswordConfirmationException(String message) {
        super(message);
    }
}
