package org.prgms.locomocoserver.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("refresh_token_expires_in") int refreshTokenExpiresIn
) {
}
