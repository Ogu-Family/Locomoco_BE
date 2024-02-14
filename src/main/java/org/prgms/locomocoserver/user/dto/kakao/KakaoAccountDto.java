package org.prgms.locomocoserver.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAccountDto(
        @JsonProperty("email") String email,
        @JsonProperty("age_range") String ageRange,
        @JsonProperty("birthyear") String birthyear,
        @JsonProperty("birthday") String birthday,
        @JsonProperty("gender") String gender
) {
}
