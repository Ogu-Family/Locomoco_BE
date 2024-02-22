package org.prgms.locomocoserver.chat.dto.request;

import lombok.NonNull;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;

public record ChatMessageRequestDto(
        @NonNull
        Long chatRoomId,
        @NonNull
        Long mogakkoId,
        @NonNull
        Long senderId,
        String message
) {

    public ChatRoom toChatRoomEntity(Mogakko mogakko, User creator) {
        return ChatRoom.builder()
                .name(mogakko.getTitle())
                .mogakko(mogakko)
                .creator(creator)
                .participants(List.of(creator))
                .build();
    }

    public ChatMessage toChatMessageEntity(User sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(message).build();
    }

}
