package org.prgms.locomocoserver.chat.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;


public record ChatMessageRequestDto(
        @Nonnull
        Long chatRoomId,
        @Nonnull
        Long mogakkoId,
        @Nonnull
        Long senderId,
        @NotBlank
        @Length(max = 255, message = "메시지의 길이는 255글자를 초과할 수 없습니다.")
        String message // TODO: 글자수 초과 시 예외 처리
) {

    public ChatRoom toChatRoomEntity(Mogakko mogakko, User creator) {
        return ChatRoom.builder()
                .name(mogakko.getTitle())
                .mogakko(mogakko)
                .creator(creator)
                .build();
    }

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
