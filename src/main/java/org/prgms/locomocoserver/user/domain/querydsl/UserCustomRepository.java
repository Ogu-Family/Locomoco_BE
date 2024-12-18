package org.prgms.locomocoserver.user.domain.querydsl;

import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {
    List<User> findAllWithImageByIdIn(List<Long> userIds);
    List<User> findAllParticipantsByMogakko(Mogakko mogakko);
    Optional<User> findUserAndImageByUserIdAndDeletedAtIsNull(Long userId);
}
