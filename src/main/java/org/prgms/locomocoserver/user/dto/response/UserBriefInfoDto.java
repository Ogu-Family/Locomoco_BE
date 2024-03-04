package org.prgms.locomocoserver.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.image.dto.ImageDto;
import org.prgms.locomocoserver.user.domain.User;

public record UserBriefInfoDto(@Schema(description = "유저 id", example = "1") Long userId,
                               @Schema(description = "유저 닉네임", example = "로코모코") String nickname,
                               @Schema(description = "유저 프로필 이미지", example = "") ImageDto profileImage
                               ) {

    public static UserBriefInfoDto of(User user) {
        return new UserBriefInfoDto(user.getId(), user.getNickname(), ImageDto.of(user.getProfileImage()));
    }
}
