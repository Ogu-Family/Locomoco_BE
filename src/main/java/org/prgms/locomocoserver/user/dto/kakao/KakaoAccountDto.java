package org.prgms.locomocoserver.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAccountDto(
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        @JsonProperty("email") String email
) {
}
