package org.prgms.locomocoserver.blacklist.domain;

import java.util.List;
import java.util.Optional;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    @Query(value = "SELECT * FROM blacklists "
        + "WHERE id > :cursor AND blocker_id = :userId "
        + "ORDER BY blocked_id LIMIT 20",
        nativeQuery = true)
    List<Blacklist> findAllByBlockUserId(Long cursor, Long userId);

    Optional<Blacklist> findByBlockUserAndBlockedUser(User blockUser, User blockedUser);
}
