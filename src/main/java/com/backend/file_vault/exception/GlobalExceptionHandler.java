package com.backend.file_vault.exception;

import com.backend.file_vault.exception.dataAlreadyExistsExceptions.EmailAlreadyExistsException;
import com.backend.file_vault.exception.dataNotFoundExceptions.AppAccountNotFoundException;
import com.backend.file_vault.exception.dataNotFoundExceptions.FileMetadataNotFoundException;
import com.backend.file_vault.exception.invalidRequestExceptions.IncorrectPasswordException;
import com.backend.file_vault.exception.invalidRequestExceptions.PasswordConfirmationException;
import com.backend.file_vault.exception.storageExceptions.StorageException;
import com.backend.file_vault.model.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailAlreadyExistsException.class})
    public ResponseEntity<ErrorResponseDto> dataAlreadyExistsExceptionHandler(RuntimeException exception) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Data Conflict",
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            FileMetadataNotFoundException.class,
            AppAccountNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> dataNotFoundExceptionHandler(RuntimeException exception) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            IncorrectPasswordException.class,
            PasswordConfirmationException.class})
    public ResponseEntity<ErrorResponseDto> incorrectRequestExceptionHandler(RuntimeException exception) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Incorrect Request",
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> illegalArgumentExceptionHandler(RuntimeException exception) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Illegal Argument",
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponseDto> storageExceptionHandler(RuntimeException exception) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "File storage operation failed",
                exception.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> validationExceptionHandler(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String field = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(field, message);
                });

        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
