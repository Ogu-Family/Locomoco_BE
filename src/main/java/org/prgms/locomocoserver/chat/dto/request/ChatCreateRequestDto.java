package org.prgms.locomocoserver.chat.dto.request;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

public record ChatCreateRequestDto(Mogakko mogakko, User creator) {

    public ChatRoom toChatRoomEntity() {
        return ChatRoom.builder()
            .name(mogakko.getTitle())
            .mogakko(mogakko)
            .creator(creator)
            .build();
    }
}
