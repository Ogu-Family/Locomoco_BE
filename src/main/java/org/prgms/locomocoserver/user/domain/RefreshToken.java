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

    private String refreshToken;
    private long refreshTokenExpiresIn;

    @Indexed
    private String accessToken;
    private long accessTokenExpiresIn;

    @Builder
    public RefreshToken(String refreshToken, long refreshTokenExpiresIn, String accessToken, long accessTokenExpiresIn) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.accessToken = accessToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
