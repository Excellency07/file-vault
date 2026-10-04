package com.backend.file_vault.service;

import com.backend.file_vault.dao.entity.Account;
import com.backend.file_vault.dao.entity.FileMetaData;
import com.backend.file_vault.dao.repository.AccountRepository;
import com.backend.file_vault.dao.repository.FileRepository;
import com.backend.file_vault.exception.dataAlreadyExistsExceptions.EmailAlreadyExistsException;
import com.backend.file_vault.exception.dataNotFoundExceptions.AppAccountNotFoundException;
import com.backend.file_vault.exception.invalidRequestExceptions.IncorrectPasswordException;
import com.backend.file_vault.exception.invalidRequestExceptions.PasswordConfirmationException;
import com.backend.file_vault.exception.storageExceptions.StorageException;
import com.backend.file_vault.model.dto.accountDtos.AccountResponseDto;
import com.backend.file_vault.model.dto.accountDtos.ResetPasswordRequestDto;
import com.backend.file_vault.model.dto.accountDtos.UpdateAccountDetailsRequestDto;
import com.backend.file_vault.model.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(UUID id){
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppAccountNotFoundException("Account not found"));
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccountByEmail(String email){
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppAccountNotFoundException("Account not found"));
        return accountMapper.toDto(account);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppAccountNotFoundException("Account not found"));

        List<FileMetaData> files = fileRepository.findByAccountId(id);
        for (FileMetaData file : files) {
            try {
                fileStorageService.deleteFile(file.getStoredFilename());
            } catch (Exception e) {
                throw new StorageException("Failed to delete file from MinIO: " + file.getOriginalFilename());
            }
        }

        fileRepository.deleteAll(files);
        accountRepository.delete(account);
    }

    @Transactional
    public AccountResponseDto updateAccountDetails(UUID id, UpdateAccountDetailsRequestDto request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppAccountNotFoundException("Account not found"));

        String updatedEmail;
        String updatedUsername;

        if (request.email() != null && !request.email().isBlank() && !request.email().equals(account.getEmail())) {
            updatedEmail = request.email();
            if (accountRepository.existsByEmail(updatedEmail))
                throw new EmailAlreadyExistsException("Email already exists.");
            account.setEmail(updatedEmail);
        }

        if (request.name() != null && !request.name().isBlank() && !request.name().equals(account.getName())) {
            updatedUsername = request.name();
            account.setName(updatedUsername);
        }

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    public void resetPassword(UUID id, ResetPasswordRequestDto request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppAccountNotFoundException("Account not found"));

        if (!passwordEncoder.matches(request.oldPassword(), account.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password.");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordConfirmationException("Password confirmation failed.");
        }

        account.setPassword(passwordEncoder.encode(request.newPassword()));
        accountRepository.save(account);
    }
}
