package org.prgms.locomocoserver.chat.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId AND cm.deletedAt IS NULL ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findAllByChatRoomId(Long chatRoomId, Pageable pageable);
}
