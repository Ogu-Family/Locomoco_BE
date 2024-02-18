package org.prgms.locomocoserver.chat.dto;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

public record ChatRoomDto(
        Long roomId,
        String name,
        Set<WebSocketSession> sessions
) {
    public ChatRoomDto(Long roomId, String name) {
        this(roomId, name, new HashSet<>());
    }
}
