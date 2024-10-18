package org.prgms.locomocoserver.chat.dao;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record ChatActivityDao(
        String unReadMsgCnt,
        String chatRoomId,
        ObjectId chatMessageId,
        String senderId,
        String message,
        LocalDateTime createdAt
) {
}
