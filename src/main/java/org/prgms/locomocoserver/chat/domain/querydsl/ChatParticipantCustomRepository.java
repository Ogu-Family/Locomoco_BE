package org.prgms.locomocoserver.chat.domain.querydsl;

import org.prgms.locomocoserver.chat.domain.ChatParticipant;

import java.util.Optional;

public interface ChatParticipantCustomRepository {
    Optional<ChatParticipant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
