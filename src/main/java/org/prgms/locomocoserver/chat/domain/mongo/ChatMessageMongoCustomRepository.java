package org.prgms.locomocoserver.chat.domain.mongo;

import java.util.List;
import java.util.Optional;

public interface ChatMessageMongoCustomRepository {
    Optional<ChatMessageMongo> findLatestMessageByRoomId(Long roomId);

    List<ChatMessageMongo> findAllChatMessagesByRoomId(Long roomId, String cursorValue, int pageSize);

    int unReadMessageCount(Long roomId, String lastReadMsgId);

    void deleteAllChatMessages(Long roomId);
}
