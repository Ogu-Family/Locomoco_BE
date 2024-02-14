package org.prgms.locomocoserver.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;
import org.prgms.locomocoserver.user.domain.enums.Provider;

import java.time.LocalDate;

public record UserDto(
        @Schema(description = "사용자 id", example = "1L")
        Long userId,
        @Schema(description = "사용자 닉네임", example = "nickname")
        String nickname,
        @Schema(description = "사용자 생년월일", example = "2002-02-25")
        LocalDate birth,
        @Schema(description = "사용자 성별", example = "FEMALE")
        Gender gender,
        @Schema(description = "사용자 온도", example = "36.5")
        double temperature,
        @Schema(description = "사용자 직업", example = "DEVELOPER")
        Job job,
        @Schema(description = "사용자 email", example = "example@gmail.com")
        String email,
        @Schema(description = "로그인 방법", example = "KAKAO")
        Provider provider
) {
}
