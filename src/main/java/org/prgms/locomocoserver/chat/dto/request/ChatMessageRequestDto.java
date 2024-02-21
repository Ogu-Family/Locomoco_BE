package org.prgms.locomocoserver.chat.dto.request;

import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

public record ChatMessageRequestDto(
        Long chatRoomId,
        Long mogakkoId,
        Long senderId,
        String message
) {

    public ChatRoom toChatRoomEntity(Mogakko mogakko, User creator) {
        return ChatRoom.builder()
                .name(mogakko.getTitle())
                .mogakko(mogakko)
                .creator(creator)
                .build();
    }

    public ChatMessage toChatMessageEntity() {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(message)
                .build();
    }

}
