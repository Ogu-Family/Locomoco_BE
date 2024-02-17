package org.prgms.locomocoserver.user.dto.response;

import org.prgms.locomocoserver.user.domain.User;

public record UserBriefInfoDto(Long userId,
                               String nickname
                               // TODO: 프로필 사진 처리
                               ) {

    public static UserBriefInfoDto create(User user) {
        return new UserBriefInfoDto(user.getId(), user.getNickname());
    }
}
