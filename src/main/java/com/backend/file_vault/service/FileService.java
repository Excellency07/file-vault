package com.backend.file_vault.service;

import com.backend.file_vault.dao.entity.FileMetaData;
import com.backend.file_vault.dao.repository.AccountRepository;
import com.backend.file_vault.dao.repository.FileRepository;
import com.backend.file_vault.exception.dataNotFoundExceptions.AppAccountNotFoundException;
import com.backend.file_vault.exception.dataNotFoundExceptions.FileMetadataNotFoundException;
import com.backend.file_vault.model.dto.PageResponseDto;
import com.backend.file_vault.model.dto.fileDtos.FileResponseDto;
import com.backend.file_vault.model.mapper.FileMapper;
import com.backend.file_vault.specification.FileSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final Tika tika;
    private final FileMapper fileMapper;
    private final AccountRepository accountRepository;

    @Transactional
    public FileResponseDto uploadFile(MultipartFile file, UUID accountId) {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file is not allowed");

        String type;
        try {
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            type = tika.detect(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String storedFilename = UUID.randomUUID() + ".bin";
        try {
            fileStorageService.uploadFile(storedFilename, file.getInputStream(), file.getSize(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        originalFilename.replaceAll("[^A-Za-z0-9.-_]]", "_");

        FileMetaData fileMetaData = new FileMetaData(
                originalFilename,
                storedFilename,
                type,
                file.getSize(),
                accountId
        );
        return fileMapper.toDto(fileRepository.save(fileMetaData));
    }

    @Transactional
    public InputStream downloadFile(UUID fileId, UUID accountId) {
        FileMetaData fileMetaData = fileRepository.findByIdAndAccountId(fileId, accountId)
                .orElseThrow(() -> new FileMetadataNotFoundException("File not found"));

        try {
            return fileStorageService.downloadFile(fileMetaData.getStoredFilename(), fileMetaData.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteFile(UUID fileId, UUID accountId) {
        FileMetaData fileMetaData = fileRepository.findByIdAndAccountId(fileId, accountId)
                .orElseThrow(() -> new FileMetadataNotFoundException("File not found"));

        fileRepository.delete(fileMetaData);
        try {
            fileStorageService.deleteFile(fileMetaData.getStoredFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public FileResponseDto getFileById(UUID fileId, UUID accountId) {
        FileMetaData fileMetaData = fileRepository.findByIdAndAccountId(fileId, accountId)
                .orElseThrow(() -> new FileMetadataNotFoundException("File not found"));
        return fileMapper.toDto(fileMetaData);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<FileResponseDto> getAllFiles(UUID accountId, Pageable pageable, String keyword, Long minSize, Long maxSize, Instant dateFrom, Instant dateTo) {
        if (!accountRepository.existsById(accountId)) throw new AppAccountNotFoundException("Account not found");
        Specification<FileMetaData> specification = FileSpecification.getSpecification(accountId, keyword, minSize, maxSize, dateFrom, dateTo);
        Page<FileMetaData> page = fileRepository.findAll(specification, pageable);
        return PageResponseDto.toDto(page.map(fileMapper::toDto));
    }
}
