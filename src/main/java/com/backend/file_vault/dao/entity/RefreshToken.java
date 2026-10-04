package com.backend.file_vault.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("RefreshToken")
public class RefreshToken {

    @Id
    private String token;

    @Indexed
    private String familyId;

    @Indexed
    private UUID accountId;

    private boolean used;

    @TimeToLive
    private Long expirationTime;

    public RefreshToken(String token, String familyId, UUID accountId, Long expirationTime) {
        this(token, familyId, accountId, false, expirationTime);
    }
}
