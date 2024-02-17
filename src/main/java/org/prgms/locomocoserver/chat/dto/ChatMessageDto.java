package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.enums.MessageType;

public record ChatMessageDto(
        MessageType messageType,
        Long chatRoomId,
        Long senderId,
        String message
) {
}
