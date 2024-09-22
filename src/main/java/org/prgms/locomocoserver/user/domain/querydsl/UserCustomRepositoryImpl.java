package org.prgms.locomocoserver.user.domain.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.mogakkos.domain.participants.QParticipant;
import org.prgms.locomocoserver.user.domain.QUser;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
