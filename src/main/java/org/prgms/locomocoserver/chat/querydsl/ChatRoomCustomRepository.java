package org.prgms.locomocoserver.chat.querydsl;

import org.prgms.locomocoserver.chat.domain.ChatRoom;

import java.util.List;

public interface ChatRoomCustomRepository {
    List<ChatRoom> findByParticipantsId(Long userId, Long cursorId, int pageSize);
}
