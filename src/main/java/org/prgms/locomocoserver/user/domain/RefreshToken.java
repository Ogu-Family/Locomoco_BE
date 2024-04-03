package org.prgms.locomocoserver.user.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh_token", timeToLive = 60*60*24*3)
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String accessToken;

    private String refreshToken;

    @Builder
    public RefreshToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
