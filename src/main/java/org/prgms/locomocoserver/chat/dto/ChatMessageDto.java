package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

public record ChatMessageDto(
        Long chatRoomId,
        Long mogakkoId,
        Long senderId,
        String message
) {
    public ChatMessageDto updateEnterMessage(String newMessage) {
        return new ChatMessageDto(this.chatRoomId, this.mogakkoId, this.senderId, newMessage);
    }

    public ChatRoom toChatRoomEntity(Mogakko mogakko, User creator) {
        return ChatRoom.builder()
                .name(mogakko.getTitle())
                .mogakko(mogakko)
                .creator(creator)
                .build();
    }
}

