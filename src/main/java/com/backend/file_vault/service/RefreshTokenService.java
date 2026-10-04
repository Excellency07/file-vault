package com.backend.file_vault.service;

import com.backend.file_vault.dao.entity.Account;
import com.backend.file_vault.dao.entity.RefreshToken;
import com.backend.file_vault.dao.repository.AccountRepository;
import com.backend.file_vault.dao.repository.RefreshTokenRepository;
import com.backend.file_vault.exception.dataNotFoundExceptions.AppAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    @Value("${jwt.refreshExpirationTime}")
    private long EXPIRATION_TIME;

    public RefreshToken generateRefreshToken(UUID accountId){
        if (!accountRepository.existsById(accountId)) throw new AppAccountNotFoundException("Account not found");

        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                accountId,
                EXPIRATION_TIME
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken rotateRefreshToken(String refreshToken) {
        RefreshToken existingRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()-> new RuntimeException("Token not found or expired"));

        if (existingRefreshToken.isUsed()) {
            List<RefreshToken> family = refreshTokenRepository.findByFamilyId(existingRefreshToken.getFamilyId());
            refreshTokenRepository.deleteAll(family);
            throw new RuntimeException("Replay attack detected");
        }

        existingRefreshToken.setUsed(true);
        refreshTokenRepository.save(existingRefreshToken);

        RefreshToken newRefreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                existingRefreshToken.getFamilyId(),
                existingRefreshToken.getAccountId(),
                EXPIRATION_TIME
        );
        return refreshTokenRepository.save(newRefreshToken);
    }

    public void logout(String refreshToken){
        RefreshToken existingRefreshToken = refreshTokenRepository.findByToken(refreshToken).orElse(null);
        if (existingRefreshToken != null) {
            List<RefreshToken> family = refreshTokenRepository.findByFamilyId(existingRefreshToken.getFamilyId());
            refreshTokenRepository.deleteAll(family);
        }
    }
}
