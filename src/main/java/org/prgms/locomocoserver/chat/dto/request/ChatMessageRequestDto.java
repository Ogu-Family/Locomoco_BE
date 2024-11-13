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
import java.util.List;

public record ChatMessageRequestDto(
        @Nonnull
        Long chatRoomId,
        @Nonnull
        Long senderId,
        @NotBlank @Length(max = 255, message = "글자수는 255자까지 입력 가능합니다.")
        String message,
        List<String> imageByteCode
) {
    public ChatMessage toChatMessageEntity(User sender, ChatRoom chatRoom, boolean isNotice) {
        return ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .content(message)
                .isNotice(isNotice).build();
    }

    public ChatMessageMongo toChatMessageMongo(Long chatRoomId, boolean isNotice, List<String> imageUrls) {
        return ChatMessageMongo.builder()
                .chatRoomId(chatRoomId.toString())
                .senderId(senderId.toString())
                .message(message)
                .imageUrls(imageUrls)
                .isNotice(isNotice)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
