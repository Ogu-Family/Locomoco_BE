package org.prgms.locomocoserver.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import org.prgms.locomocoserver.user.domain.enums.Gender;
import org.prgms.locomocoserver.user.domain.enums.Job;

import java.time.LocalDate;

public record UserInitInfoRequestDto(
        @Schema(description = "사용자 닉네임", example = "nickname")
        @NonNull
        String nickname,
        @Schema(description = "사용자 생년월일", example = "2002-02-25")
        @NonNull
        LocalDate birth,
        @Schema(description = "사용자 성별", example = "FEMALE")
        @NonNull
        String gender,
        @Schema(description = "사용자 직업", example = "DEVELOPER")
        @NonNull
        String job
) {
}
