package org.prgms.locomocoserver.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.user.domain.User;

public record UserMyPageDto(@Schema(description = "유저 정보") UserInfoDto userInfo,
                            @Schema(description = "좋아요(찜) 모각코 개수") long likeMogakkoCount,
                            @Schema(description = "진행중인 모각코 개수") long ongoingMogakkoCount,
                            @Schema(description = "종료된 모각코 개수") long completeMogakkoCount) {

    public static UserMyPageDto create(User user, long likeMogakkoCount, long ongoingMogakkoCount, long completeMogakkoCount) {
        return new UserMyPageDto(UserInfoDto.of(user),
            likeMogakkoCount,
            ongoingMogakkoCount,
            completeMogakkoCount);
    }
}
