package org.prgms.locomocoserver.chat.domain.querydsl;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;

public interface ChatRoomCustomRepository {
    List<ChatRoom> findByParticipantsId(Long userId, Long cursorId, int pageSize);
    List<User> findParticipantsByRoomId(Long roomId);
}
