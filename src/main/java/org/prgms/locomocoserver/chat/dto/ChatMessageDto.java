package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatMessage;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long chatMessageId,
        Long chatRoomId,
        Long senderId,
        String message,
        LocalDateTime createdAt
) {
    public static ChatMessageDto of(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage.getId(), chatMessage.getChatRoom().getId(), chatMessage.getSender().getId(), chatMessage.getContent(), chatMessage.getCreatedAt());
    }
}

