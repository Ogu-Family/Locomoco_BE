package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomDto(
        Long roomId,
        Long mogakkoId,
        String name,
        int participantCnt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ChatMessageDto lastMessage
) {
    public static ChatRoomDto of(ChatRoom chatRoom, ChatMessageDto lastMessage) {
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getMogakko().getId(), chatRoom.getName(), chatRoom.getChatParticipants().size(), chatRoom.getCreatedAt(), chatRoom.getUpdatedAt(), lastMessage);
    }
}
