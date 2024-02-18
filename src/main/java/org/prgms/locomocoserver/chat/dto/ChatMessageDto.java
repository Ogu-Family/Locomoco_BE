package org.prgms.locomocoserver.chat.dto;

public record ChatMessageDto(
        Long chatRoomId,
        Long senderId,
        String message
) {
    public ChatMessageDto updateEnterMessage(String newMessage) {
        return new ChatMessageDto(this.chatRoomId, this.senderId, newMessage);
    }
}

