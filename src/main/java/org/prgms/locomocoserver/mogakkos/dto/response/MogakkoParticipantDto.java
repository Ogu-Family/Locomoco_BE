package org.prgms.locomocoserver.mogakkos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.image.dto.ImageDto;
import org.prgms.locomocoserver.user.domain.User;

public record MogakkoParticipantDto(@Schema(description = "참여자 id", example = "2412") Long userId,
                                    @Schema(description = "참여자 닉네임", example = "ZZAMBA") String nickname,
                                    @Schema(description = "참여자 프로필 이미지") ImageDto profileImage) {

    public static MogakkoParticipantDto create(User user) {
        return new MogakkoParticipantDto(user.getId(), user.getNickname(), ImageDto.of(user.getProfileImage()));
    }
}
