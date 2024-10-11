package org.prgms.locomocoserver.chat.domain.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.QChatParticipant;
import org.prgms.locomocoserver.chat.domain.QChatRoom;
import org.prgms.locomocoserver.image.domain.QImage;
import org.prgms.locomocoserver.user.domain.QUser;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .where(
                        chatParticipant.user.id.eq(userId)
                                .and(chatRoom.id.lt(cursorId))
                )
                .orderBy(chatRoom.updatedAt.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<User> findParticipantsByRoomId(Long roomId) {
        QUser user = QUser.user;
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;
        QImage image = QImage.image;

        return queryFactory
                .selectFrom(user)
                .join(chatParticipant).on(chatParticipant.user.id.eq(user.id))
                .leftJoin(user.profileImage, image)
                .fetchJoin()
                .where(chatParticipant.chatRoom.id.eq(roomId).and(user.deletedAt.isNotNull()))
                .fetch();
    }

    @Override
    @Transactional
    public void softDeleteParticipantsByRoomId(Long roomId) {
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;

        queryFactory
                .update(chatParticipant)
                .set(chatParticipant.deletedAt, LocalDateTime.now())
                .where(chatParticipant.chatRoom.id.eq(roomId))
                .execute();
    }
}
