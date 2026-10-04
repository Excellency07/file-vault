package com.backend.file_vault.dao.repository;

import com.backend.file_vault.dao.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetaData, UUID>, JpaSpecificationExecutor<FileMetaData> {
    Optional<FileMetaData> findByIdAndAccountId(UUID id, UUID accountId);

    List<FileMetaData> findByAccountId(UUID id);
}
