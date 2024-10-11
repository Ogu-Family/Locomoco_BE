package org.prgms.locomocoserver.user.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh_token", timeToLive = RefreshToken.TOKEN_TTL)
public class RefreshToken {

    public static final long TOKEN_TTL = 60 * 60 * 24 * 3; // 3일

    @Id
    private String refreshToken;

    @Indexed
    private String accessToken;

    @Builder
    public RefreshToken(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
