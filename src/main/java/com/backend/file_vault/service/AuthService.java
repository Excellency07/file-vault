package com.backend.file_vault.service;

import com.backend.file_vault.dao.entity.Account;
import com.backend.file_vault.dao.entity.RefreshToken;
import com.backend.file_vault.dao.repository.AccountRepository;
import com.backend.file_vault.exception.dataAlreadyExistsExceptions.EmailAlreadyExistsException;
import com.backend.file_vault.model.dto.accountDtos.AccountResponseDto;
import com.backend.file_vault.model.dto.accountDtos.AuthResponseDto;
import com.backend.file_vault.model.dto.accountDtos.LoginRequestDto;
import com.backend.file_vault.model.dto.accountDtos.RegisterAccountRequestDto;
import com.backend.file_vault.model.mapper.AccountMapper;
import com.backend.file_vault.security.JwtService;
import com.backend.file_vault.security.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AccountResponseDto register(RegisterAccountRequestDto request) {
        if (accountRepository.existsByEmail(request.email())) throw new EmailAlreadyExistsException("Email already exists");
        Account account = accountMapper.toEntity(request);
        account.setPassword(passwordEncoder.encode(request.password()));
        accountRepository.save(account);
        return accountMapper.toDto(account);
    }

    public AuthResponseDto verifyLogin(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        AccountDetails accountDetails = (AccountDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(accountDetails);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(accountDetails.getId());
        return new AuthResponseDto(
                token,
                refreshToken.getToken(),
                accountDetails.getUsername(),
                jwtService.getEXPIRATION_TIME()
        );
    }
}
