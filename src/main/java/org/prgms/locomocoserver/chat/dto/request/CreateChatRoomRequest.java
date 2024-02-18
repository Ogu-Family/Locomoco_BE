package org.prgms.locomocoserver.chat.dto.request;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

public record CreateChatRoomRequest(
        String name,
        Long mogakkoId,
        Long creatorId
) {
    public ChatRoom toEntity(Mogakko mogakko, User creator) {
        return ChatRoom.builder()
                .name(this.name())
                .mogakko(mogakko)
                .creator(creator)
                .build();
    }
}
