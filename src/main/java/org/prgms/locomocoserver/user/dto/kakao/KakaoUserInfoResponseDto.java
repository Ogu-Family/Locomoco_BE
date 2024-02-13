package org.prgms.locomocoserver.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponseDto(
        @JsonProperty("id") long id,
        @JsonProperty("connected_at") String connectedAt,
        @JsonProperty("kakao_account") KakaoAccountDto kakaoAccount

) implements OAuthUserInfoDto {

    private static double DEFAULT_TEMPERATURE = 36.5;

    @Override
    public User toEntity() {
        return User.builder()
                .birth(LocalDate.parse(kakaoAccount.birthyear() + kakaoAccount.birthday(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .gender(Gender.valueOf(kakaoAccount.gender().toUpperCase()))
                .temperature(DEFAULT_TEMPERATURE)
                .email(this.getEmail())
                .provider(this.getProvider())
                .build();
    }

    @Override
    public String getProvider() {
        return Provider.KAKAO.name();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.email();
    }
}
