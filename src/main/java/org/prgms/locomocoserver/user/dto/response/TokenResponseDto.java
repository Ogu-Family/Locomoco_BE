package org.prgms.locomocoserver.user.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponseDto(
        @JsonProperty("access_token")
        @Schema(description = "엑세스 토큰", example = "ddfdacdksij-ekr91")
        String accessToken,
        @Schema(description = "토큰 타입", example = "bearer")
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("refresh_token")
        @Schema(description = "리프레시 토큰", example = "a3fd_9cdksij-ekr91")
        String refreshToken,
        @JsonProperty("expires_in")
        @Schema(description = "엑세스 토큰 만료", example = "28000")
        int expiresIn,
        @JsonProperty("refresh_token_expires_in")
        @Schema(description = "리프레시 토큰 만료", example = "28000")
        int refreshTokenExpiresIn
) {
}
