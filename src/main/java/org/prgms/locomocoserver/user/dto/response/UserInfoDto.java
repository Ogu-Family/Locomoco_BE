package org.prgms.locomocoserver.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.image.dto.ImageDto;
import org.prgms.locomocoserver.tags.domain.Tag;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Gender;

import java.time.LocalDate;

public record UserInfoDto(
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
        @Schema(description = "사용자 직업 태그 id", example = "1")
        Long jobId,
        @Schema(description = "사용자 email", example = "example@gmail.com")
        String email,
        @Schema(description = "사용자 프로필 사진")
        ImageDto profileImage,
        @Schema(description = "로그인 방법", example = "KAKAO")
        String provider
) {
        public static UserInfoDto of(User user) {
                Long jobTagId = user.getJobTag() == null ? null : user.getJobTag().getId();
                return new UserInfoDto(
                        user.getId(),
                        user.getNickname(),
                        user.getBirth(),
                        user.getGender(),
                        user.getTemperature(),
                        jobTagId,
                        user.getEmail(),
                        ImageDto.of(user.getProfileImage()),
                        user.getProvider()
                );
        }
}
