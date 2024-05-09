package org.prgms.locomocoserver.chat.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.user.domain.User;

import java.time.LocalDateTime;

public record ChatMessageRequestDto(
        @Nonnull
        Long chatRoomId,
        @Nonnull
        Long senderId,
        @NotBlank @Length(max = 255, message = "글자수는 255자까지 입력 가능합니다.")
        String message
) {
    public ChatMessage toChatMessageEntity(User sender, ChatRoom chatRoom, boolean isNotice) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(message)
                .isNotice(isNotice).build();
    }

    public ChatMessageMongo toChatMessageMongo(User sender, ChatRoom chatRoom, boolean isNotice) {
        String imagePath = sender.getProfileImage() != null ? imagePath = sender.getProfileImage().getPath() : null;
        return ChatMessageMongo.builder()
               .senderId(senderId.toString())
               .senderNickname(sender.getNickname())
               .senderImage(imagePath)
               .message(message)
               .isNotice(isNotice)
               .createdAt(LocalDateTime.now())
               .build();
    }
}
