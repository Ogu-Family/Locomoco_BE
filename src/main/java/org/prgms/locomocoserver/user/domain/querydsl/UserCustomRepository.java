package org.prgms.locomocoserver.user.domain.querydsl;

import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;

public interface UserCustomRepository {
    List<User> findAllParticipantsByMogakko(Mogakko mogakko);
}
