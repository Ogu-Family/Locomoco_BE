package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public record ChatRoomDto(
        Long roomId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ChatRoomDto of(ChatRoom chatRoom) {
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName(), chatRoom.getCreatedAt(), chatRoom.getUpdatedAt());
    }
}
