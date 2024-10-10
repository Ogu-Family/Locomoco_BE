package org.prgms.locomocoserver.chat.domain.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.QChatParticipant;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatParticipantCustomRepositoryImpl implements ChatParticipantCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatParticipant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;

        return Optional.ofNullable(queryFactory
                .selectFrom(chatParticipant)
                .where(chatParticipant.user.id.eq(userId).and(chatParticipant.chatRoom.id.eq(chatRoomId)))
                .fetchOne());
    }
}
