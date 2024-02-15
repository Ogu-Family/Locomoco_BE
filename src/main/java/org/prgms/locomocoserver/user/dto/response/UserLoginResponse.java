package org.prgms.locomocoserver.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginResponse(
        TokenResponseDto tokenResponseDto,
        UserInfoDto userInfoDto,
        @Schema(description = "첫 로그인 여부", example = "true")
        boolean isNewUser
) {
}
