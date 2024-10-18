package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomDto(
        Long roomId,
        Long mogakkoId,
        String name,
        int participantCnt,
        int unReadMsgCnt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ChatMessageBriefDto lastMessage,
        ChatUserInfo userInfo
) {
    public static ChatRoomDto of(ChatRoom chatRoom, int unReadMsgCnt, ChatMessageBriefDto lastMessage, ChatUserInfo chatUserInfo) {
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getMogakko().getId(), chatRoom.getName(), chatRoom.getChatParticipants().size(), unReadMsgCnt, chatRoom.getCreatedAt(), chatRoom.getUpdatedAt(), lastMessage, chatUserInfo);
    }
}
