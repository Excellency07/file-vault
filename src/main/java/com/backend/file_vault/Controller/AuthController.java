package com.backend.file_vault.Controller;

import com.backend.file_vault.dao.entity.Account;
import com.backend.file_vault.dao.entity.RefreshToken;
import com.backend.file_vault.dao.repository.AccountRepository;
import com.backend.file_vault.exception.dataNotFoundExceptions.AppAccountNotFoundException;
import com.backend.file_vault.model.dto.accountDtos.*;
import com.backend.file_vault.security.AccountDetails;
import com.backend.file_vault.security.JwtService;
import com.backend.file_vault.service.AuthService;
import com.backend.file_vault.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDto> register(@RequestBody RegisterAccountRequestDto request) {
        AccountResponseDto accountResponseDto = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        AuthResponseDto authResponseDto = authService.verifyLogin(request);
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        RefreshToken refreshToken = refreshTokenService.rotateRefreshToken(request.refreshToken());
        Account account = accountRepository.findById(refreshToken.getAccountId())
                .orElseThrow(() -> new AppAccountNotFoundException("Account not found"));

        String accessToken = jwtService.generateToken(new AccountDetails(account));
        RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto(accessToken, refreshToken.getToken());
        return ResponseEntity.ok(refreshTokenResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDto request) {
        refreshTokenService.logout(request.refreshToken());
        return ResponseEntity.ok("Logged out");
    }

}
