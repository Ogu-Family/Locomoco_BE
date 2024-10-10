package org.prgms.locomocoserver.user.domain.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.domain.QImage;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.participants.QParticipant;
import org.prgms.locomocoserver.user.domain.QUser;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findAllParticipantsByMogakko(Mogakko mogakko) {
        QUser user = QUser.user;
        QParticipant participant = QParticipant.participant;

        return queryFactory
                .selectFrom(user)
                .join(participant).on(participant.user.eq(user))
                .fetchJoin()
                .where(participant.mogakko.eq(mogakko))
                .fetch();
    }

    @Override
    public Optional<User> findUserAndImageByUserIdAndDeletedAtIsNull(Long userId) {
        QUser user = QUser.user;
        QImage image = QImage.image;

        User result = queryFactory
                .selectFrom(user)
                .join(image).on(user.profileImage.id.eq(image.id))
                .fetchJoin()
                .where(user.id.eq(userId)
                        .and(user.deletedAt.isNull())) // deletedAt이 null인 경우 추가
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
