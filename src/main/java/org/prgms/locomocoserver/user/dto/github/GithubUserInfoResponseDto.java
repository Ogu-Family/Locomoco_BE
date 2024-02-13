package org.prgms.locomocoserver.user.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubUserInfoResponseDto(
        @JsonProperty("email") String email,
        @JsonProperty("primary") boolean primary
) implements OAuthUserInfoDto {
    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getProvider() {
        return Provider.GITHUB.name();
    }

    @Override
    public User toEntity() {
        return User.builder()
                .temperature(DEFAULT_TEMPERATURE)
                .email(this.getEmail())
                .provider(this.getProvider())
                .build();
    }
}
