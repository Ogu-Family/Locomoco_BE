package org.prgms.locomocoserver.chat.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.QChatParticipant;
import org.prgms.locomocoserver.chat.domain.QChatRoom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findByParticipantsId(Long userId, Long cursorId, int pageSize) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;

        return queryFactory
                .selectFrom(chatRoom)
                .join(chatRoom.chatParticipants, chatParticipant)
                .fetchJoin()
                .where(
                        chatParticipant.user.id.eq(userId)
                                .and(chatRoom.id.lt(cursorId))
                )
                .orderBy(chatRoom.id.desc())
                .limit(pageSize)
                .fetch();

    }
}
