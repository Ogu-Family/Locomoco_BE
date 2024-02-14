package org.prgms.locomocoserver.user.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubUserInfoResponseDto(
        @Schema(description = "사용자 email", example = "example@gmail.com")
        @JsonProperty("email") String email,
        @Schema(description = "사용자 주사용 이메일 여부", example = "true")
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
