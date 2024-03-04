package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public record ChatRoomDto(
        Long roomId,
        String name,
        int participantCnt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ChatMessageDto lastMessage
) {
    public static ChatRoomDto of(ChatRoom chatRoom, ChatMessageDto lastMessage) {
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName(), chatRoom.getParticipants().size(), chatRoom.getCreatedAt(), chatRoom.getUpdatedAt(), lastMessage);
    }
}
