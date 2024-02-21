package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatMessage;

public record ChatMessageDto(
        Long chatMessageId,
        Long chatRoomId,
        Long senderId,
        String message
) {
    public static ChatMessageDto of(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage.getId(), chatMessage.getChatRoom().getId(), chatMessage.getSender().getId(), chatMessage.getContent());
    }
}

