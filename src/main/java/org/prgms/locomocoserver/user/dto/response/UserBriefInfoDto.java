package org.prgms.locomocoserver.user.dto.response;

public record UserBriefInfoDto(Long userId,
                               String nickname
                               // TODO: 프로필 사진 처리
                               ) {

}
