package org.prgms.locomocoserver.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    void deleteAllByChatRoom(ChatRoom chatRoom);
}
