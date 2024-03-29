package org.prgms.locomocoserver.chat.domain.chatParticipant;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    void deleteAllByChatRoom(ChatRoom chatRoom);
    void deleteByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}
