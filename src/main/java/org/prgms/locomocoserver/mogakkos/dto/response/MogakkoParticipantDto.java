package org.prgms.locomocoserver.mogakkos.dto.response;

import org.prgms.locomocoserver.user.domain.User;

public record MogakkoParticipantDto(Long userId,
                                    String nickname) {

    public static MogakkoParticipantDto create(User user) {
        return new MogakkoParticipantDto(user.getId(), user.getNickname());
    }
}
