package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.user.domain.User;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long chatMessageId,
        Long chatRoomId,
        Long senderId,
        String senderNickName,
        String senderProfileImage,
        String message,
        LocalDateTime createdAt
) {
    public static ChatMessageDto of(ChatMessage chatMessage) {
        User sender = chatMessage.getSender();
        String profileImagePath = null;
        if (sender.getProfileImage() != null) {
            profileImagePath = sender.getProfileImage().getPath();
        }
        return new ChatMessageDto(chatMessage.getId(), chatMessage.getChatRoom().getId(), sender.getId(),
                sender.getNickname(), profileImagePath, chatMessage.getContent(), chatMessage.getCreatedAt());
    }
}

