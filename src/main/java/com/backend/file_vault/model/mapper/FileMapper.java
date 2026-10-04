package com.backend.file_vault.model.mapper;

import com.backend.file_vault.dao.entity.FileMetaData;
import com.backend.file_vault.model.dto.fileDtos.FileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileResponseDto toDto(FileMetaData fileMetaData);
}
