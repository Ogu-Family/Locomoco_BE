package org.prgms.locomocoserver.chat.domain.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.QChatParticipant;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatParticipantCustomRepositoryImpl implements ChatParticipantCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Optional<ChatParticipant> save(ChatParticipant chatParticipant) {
        if (chatParticipant.getId() == null) {
            entityManager.persist(chatParticipant); // 새 엔티티 저장
        } else {
            entityManager.merge(chatParticipant); // 기존 엔티티 업데이트
        }
        return Optional.of(chatParticipant);
    }

    @Override
    public Optional<ChatParticipant> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;

        return Optional.ofNullable(queryFactory
                .selectFrom(chatParticipant)
                .where(chatParticipant.user.id.eq(userId).and(chatParticipant.chatRoom.id.eq(chatRoomId)))
                .fetchOne());
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

    @Override
    @Transactional
    public void deleteByChatRoomIdAndUserId(Long userId, Long chatRoomId) {
        QChatParticipant chatParticipant = QChatParticipant.chatParticipant;

        queryFactory
                .update(chatParticipant)
                .set(chatParticipant.deletedAt, LocalDateTime.now())  // 소프트 딜리트 처리
                .where(chatParticipant.user.id.eq(userId)
                        .and(chatParticipant.chatRoom.id.eq(chatRoomId)))
                .execute();
    }
}
