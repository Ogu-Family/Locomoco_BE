package org.prgms.locomocoserver.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageBriefDto(
        String chatRoomId,
        String chatMessageId,
        String senderId,
        String message,
        LocalDateTime createdAt
) {
    public static ChatMessageBriefDto of(String chatRoomId, String chatMessageId, String senderId, String message, LocalDateTime createdAt) {
        return new ChatMessageBriefDto(chatRoomId, chatMessageId, senderId, message, createdAt);
    }
}
