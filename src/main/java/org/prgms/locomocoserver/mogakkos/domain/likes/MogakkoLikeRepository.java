package org.prgms.locomocoserver.mogakkos.domain.likes;

import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MogakkoLikeRepository extends JpaRepository<MogakkoLike, Long> {
    Optional<MogakkoLike> findByMogakkoAndUser(Mogakko mogakko, User user);
}
