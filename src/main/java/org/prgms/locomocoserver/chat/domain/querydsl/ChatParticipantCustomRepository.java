package org.prgms.locomocoserver.chat.domain.querydsl;

import org.prgms.locomocoserver.chat.domain.ChatParticipant;

import java.util.Optional;

public interface ChatParticipantCustomRepository {
    Optional<ChatParticipant> save(ChatParticipant chatParticipant);
    Optional<ChatParticipant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
    void softDeleteParticipantsByRoomId(Long roomId);
    void deleteByChatRoomIdAndUserId(Long userId, Long chatRoomId);
}
