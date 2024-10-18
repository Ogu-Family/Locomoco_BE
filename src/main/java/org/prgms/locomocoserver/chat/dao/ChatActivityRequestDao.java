package org.prgms.locomocoserver.chat.dao;

import org.bson.types.ObjectId;

public record ChatActivityRequestDao(
        String chatRoomId,
        ObjectId lastReadMsgId
) {
}
