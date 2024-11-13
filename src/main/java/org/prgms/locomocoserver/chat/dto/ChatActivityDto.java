package org.prgms.locomocoserver.chat.dto;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record ChatActivityDto(
        String unReadMsgCnt,
        String chatRoomId,
        ObjectId chatMessageId,
        String senderId,
        String message,
        LocalDateTime createdAt
) {
}
