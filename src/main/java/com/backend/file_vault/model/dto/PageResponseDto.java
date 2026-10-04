package com.backend.file_vault.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int pageSize;
    private int pageNumber;
    private long totalElements;
    private int totalPageNumbers;
    private boolean isLast;

    public static <T> PageResponseDto<T> toDto(Page<T> page){
        return new PageResponseDto<>(
                page.getContent(),
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}