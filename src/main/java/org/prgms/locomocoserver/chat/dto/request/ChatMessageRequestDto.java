package org.prgms.locomocoserver.chat.dto.request;

import lombok.NonNull;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.user.domain.User;

public record ChatMessageRequestDto(
        @NonNull
        Long chatRoomId,
        @NonNull
        Long mogakkoId,
        @NonNull
        Long senderId,
        String message // TODO: 글자수 초과 시 예외 처리
) {
    public ChatMessage toChatMessageEntity(User sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
            .sender(sender)
            .chatRoom(chatRoom)
            .content(message).build();
    }

    public ChatMessage toEnterMessageEntity(User sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(sender.getNickname() + " 님이 채팅방에 입장하였습니다.").build();
    }

}
