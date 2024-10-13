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

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findByParticipantsId(Long userId, Long cursorId, int pageSize) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;
        QUser user = QUser.user;

        // 1단계: chatRoomId만 가져오는 쿼리
        List<Long> chatRoomIds = queryFactory
                .select(chatRoom.id)
                .from(chatRoom)
                .join(chatRoom.chatParticipants, chatParticipant)
                .where(
                        chatParticipant.user.id.eq(userId)
                                .and(chatRoom.id.lt(cursorId))
                                .and(chatParticipant.deletedAt.isNull())
                )
                .orderBy(chatRoom.updatedAt.desc())
                .limit(pageSize)
                .fetch();

        // 2단계: 가져온 chatRoomId로 fetch join
        return queryFactory
                .selectFrom(chatRoom)
                .join(chatRoom.chatParticipants, chatParticipant).fetchJoin()
                .join(chatParticipant.user, user).fetchJoin()
                .where(chatRoom.id.in(chatRoomIds))
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
}
