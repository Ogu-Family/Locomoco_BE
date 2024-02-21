package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatMessage;

public record ChatMessageDto(
        Long chatRoomId,
        Long senderId,
        String message
) {
    public static ChatMessageDto of(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage.getChatRoomId(), chatMessage.getSenderId(), chatMessage.getContent());
    }
}

