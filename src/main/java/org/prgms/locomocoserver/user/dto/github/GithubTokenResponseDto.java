package org.prgms.locomocoserver.user.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubTokenResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("refresh_token_expires_in") int refreshTokenExpiresIn,
        @JsonProperty("scope") String scope
) {
}
