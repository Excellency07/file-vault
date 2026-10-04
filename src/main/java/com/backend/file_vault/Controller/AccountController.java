package com.backend.file_vault.Controller;

import com.backend.file_vault.model.dto.accountDtos.AccountResponseDto;
import com.backend.file_vault.model.dto.accountDtos.ResetPasswordRequestDto;
import com.backend.file_vault.model.dto.accountDtos.UpdateAccountDetailsRequestDto;
import com.backend.file_vault.security.AccountDetails;
import com.backend.file_vault.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<AccountResponseDto> getProfile(@AuthenticationPrincipal AccountDetails accountDetails) {
        UUID id = accountDetails.getId();
        AccountResponseDto accountResponseDto = accountService.getAccountByEmail(accountDetails.getUsername());
        return ResponseEntity.ok(accountResponseDto);
    }

    @PatchMapping
    public ResponseEntity<AccountResponseDto> updateAccountDetails(@AuthenticationPrincipal AccountDetails accountDetails,
                                                                   @Valid @RequestBody UpdateAccountDetailsRequestDto request) {
        UUID id = accountDetails.getId();
        AccountResponseDto accountResponseDto = accountService.updateAccountDetails(id, request);
        return ResponseEntity.ok(accountResponseDto);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@AuthenticationPrincipal AccountDetails accountDetails,
                                              @Valid @RequestBody ResetPasswordRequestDto request){
        UUID id = accountDetails.getId();
        accountService.resetPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal AccountDetails accountDetails){
        UUID id = accountDetails.getId();
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
