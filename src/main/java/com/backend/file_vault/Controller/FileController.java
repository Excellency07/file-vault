package com.backend.file_vault.Controller;

import com.backend.file_vault.dao.entity.FileMetaData;
import com.backend.file_vault.model.dto.PageResponseDto;
import com.backend.file_vault.model.dto.fileDtos.FileResponseDto;
import com.backend.file_vault.security.AccountDetails;
import com.backend.file_vault.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponseDto> getFileById(@AuthenticationPrincipal AccountDetails accountDetails,
                                                       @PathVariable UUID fileId) {
        UUID accountId = accountDetails.getId();
        FileResponseDto file = fileService.getFileById(fileId, accountId);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<FileResponseDto>> searchFiles(
            @AuthenticationPrincipal AccountDetails accountDetails,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "5") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minSize,
            @RequestParam(required = false) Long maxSize,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo
    ) {
        UUID accountId = accountDetails.getId();

        Sort.Direction direction = (sortDirection.equals("ASC")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = switch (sortBy) {
            case "filename" -> Sort.by(direction, FileMetaData::getOriginalFilename);
            case "size" -> Sort.by(direction, FileMetaData::getSize);
            case "createdAt" -> Sort.by(direction, FileMetaData::getCreatedAt);
            default -> Sort.by(direction, FileMetaData::getId);
        };

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        PageResponseDto<FileResponseDto> files = fileService.getAllFiles(accountId, pageable, keyword, minSize, maxSize, dateFrom, dateTo);
        return ResponseEntity.ok(files);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> uploadFile(@AuthenticationPrincipal AccountDetails accountDetails,
                                                      @RequestParam("file") MultipartFile file) {
        UUID accountId = accountDetails.getId();
        FileResponseDto uploadedFile = fileService.uploadFile(file, accountId);
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@AuthenticationPrincipal AccountDetails accountDetails,
                                                    @PathVariable UUID fileId) {
        UUID accountId = accountDetails.getId();
        try {
            FileResponseDto file = fileService.getFileById(fileId, accountId);
            java.io.InputStream downloadedFile = fileService.downloadFile(file.id(), accountId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.id() + "\"")
                    // Set the correct file type (e.g., "application/pdf")
                    .contentType(org.springframework.http.MediaType.parseMediaType(file.contentType()))
                    .body(new InputStreamResource(downloadedFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@AuthenticationPrincipal AccountDetails accountDetails,
                                           @PathVariable UUID fileId) {
        UUID accountId = accountDetails.getId();
        fileService.deleteFile(fileId, accountId);
        return ResponseEntity.noContent().build();
    }
}
