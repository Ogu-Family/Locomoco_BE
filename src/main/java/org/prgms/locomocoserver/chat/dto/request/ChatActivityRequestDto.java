package org.prgms.locomocoserver.chat.dto.request;

public record ChatActivityRequestDto(
        Long userId,
        String lastReadMessageId
) {
}
