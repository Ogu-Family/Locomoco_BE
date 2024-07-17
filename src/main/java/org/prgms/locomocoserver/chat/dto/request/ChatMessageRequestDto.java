package org.prgms.locomocoserver.chat.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.user.domain.User;

import java.time.LocalDateTime;

public record ChatMessageRequestDto(
        @Nonnull
        Long chatRoomId,
        @Nonnull
        Long senderId,
        @NotBlank @Length(max = 255, message = "글자수는 255자까지 입력 가능합니다.")
        String message,
        String imageByteCode
) {
    public ChatMessage toChatMessageEntity(User sender, ChatRoom chatRoom, boolean isNotice) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(message)
                .isNotice(isNotice).build();
    }

    public ChatMessageMongo toChatMessageMongo(User sender, ChatRoom chatRoom, boolean isNotice) {
        return ChatMessageMongo.builder()
                .senderId(senderId.toString())
                .senderNickname(sender.getNickname())
                .senderImage(sender.getProfileImage().getPath())
                .message(message)
                .isNotice(isNotice)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ChatMessage toChatImageMessage(User sender, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content("IMAGE")
                .isNotice(false).build();
    }
}
