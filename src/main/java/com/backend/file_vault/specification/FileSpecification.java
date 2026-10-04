package com.backend.file_vault.specification;

import com.backend.file_vault.dao.entity.FileMetaData;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileSpecification {

    public static Specification<FileMetaData> getSpecification(
            UUID accountId,
            String keyword,
            Long minSize,
            Long maxSize,
            Instant dateFrom,
            Instant dateTo
    ){
        return new Specification<FileMetaData>() {
            @Override
            public @Nullable Predicate toPredicate(Root<FileMetaData> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (accountId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("accountId"), accountId));
                }

                if (keyword != null) {
                    Predicate predicateOriginalFilename = criteriaBuilder.like(root.get("originalFilename"), "%" + keyword + "%");
                    Predicate predicateContentType = criteriaBuilder.like(root.get("contentType"), "%" + keyword + "%");
                    predicates.add(criteriaBuilder.or(predicateOriginalFilename, predicateContentType));
                }

                if (minSize != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("size"), minSize));
                }

                if (maxSize != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("size"), maxSize));
                }

                if (dateFrom != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
                }

                if (dateTo != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), dateTo));
                }


                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
