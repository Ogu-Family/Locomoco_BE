package org.prgms.locomocoserver.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAccountDto(
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        @JsonProperty("email") String email,
        @Schema(description = "사용자 나이대", example = "20~29")
        @JsonProperty("age_range") String ageRange,
        @Schema(description = "사용자 생년", example = "2002")
        @JsonProperty("birthyear") String birthyear,
        @Schema(description = "사용자 생월일", example = "0225")
        @JsonProperty("birthday") String birthday,
        @Schema(description = "사용자 성별", example = "female")
        @JsonProperty("gender") String gender
) {
}
