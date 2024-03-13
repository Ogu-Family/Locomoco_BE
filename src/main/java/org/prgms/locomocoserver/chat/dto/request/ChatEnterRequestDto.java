package org.prgms.locomocoserver.chat.dto.request;

import org.prgms.locomocoserver.user.domain.User;

public record ChatEnterRequestDto(Long chatRoomId,
                                  User participant) {

}
