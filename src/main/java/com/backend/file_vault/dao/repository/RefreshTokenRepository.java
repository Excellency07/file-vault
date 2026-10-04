package com.backend.file_vault.dao.repository;

import com.backend.file_vault.dao.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    List<RefreshToken> findByFamilyId(String familyId);

    List<RefreshToken> findByAccountId(UUID accountId);

    Optional<RefreshToken> findByToken(String token);

    void deleteByAccountId(UUID accountId);
}
