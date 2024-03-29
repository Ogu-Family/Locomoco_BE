package org.prgms.locomocoserver.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query(value = "SELECT * FROM chat_messages WHERE chat_rooms_id = :roomId AND id < :cursor ORDER BY id DESC limit :pageSize",
            nativeQuery = true)
    List<ChatMessage> findAllByChatRoomIdAndIdGreaterThan(@Param("roomId") Long chatRoomId,
                                                          @Param("cursor") Long cursor,
                                                          int pageSize);

    @Query(value = "SELECT cm.* FROM chat_messages cm " +
            "JOIN (SELECT chat_rooms_id, MAX(id) AS last_message_id FROM chat_messages GROUP BY chat_rooms_id) AS last_msg " +
            "ON cm.chat_rooms_id = last_msg.chat_rooms_id AND cm.id = last_msg.last_message_id " +
            "WHERE cm.chat_rooms_id = :roomId",
            nativeQuery = true)
    ChatMessage findLastMessageByRoomId(Long roomId);

    List<ChatMessage> findAllByChatRoom(ChatRoom chatRoom);

}
