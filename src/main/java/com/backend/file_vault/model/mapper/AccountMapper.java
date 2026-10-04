package com.backend.file_vault.model.mapper;

import com.backend.file_vault.dao.entity.Account;
import com.backend.file_vault.model.dto.accountDtos.AccountResponseDto;
import com.backend.file_vault.model.dto.accountDtos.RegisterAccountRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Account toEntity(RegisterAccountRequestDto request);

    AccountResponseDto toDto(Account account);
}
