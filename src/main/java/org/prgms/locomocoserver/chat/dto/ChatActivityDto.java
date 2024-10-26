package org.prgms.locomocoserver.chat.dto;

import org.prgms.locomocoserver.chat.domain.mongo.ChatActivity;

public record ChatActivityDto(
        String userId,
        String chatRoomId,
        String lastReadMsgId
) {
    public static ChatActivityDto of(ChatActivity chatActivity) {
        return new ChatActivityDto(chatActivity.getUserId(), chatActivity.getChatRoomId(), chatActivity.getLastReadMsgId().toString());
    }
}
