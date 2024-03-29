package org.prgms.locomocoserver.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserInitInfoRequestDto(
        @Schema(description = "사용자 닉네임", example = "nickname")
        @NotBlank
        String nickname,
        @Schema(description = "사용자 생년월일", example = "2002-02-25")
        @NotBlank
        LocalDate birth,
        @Schema(description = "사용자 성별", example = "FEMALE")
        @NotBlank
        String gender,
        @Schema(description = "사용자 직업", example = "1")
        long jobId
) {
}
