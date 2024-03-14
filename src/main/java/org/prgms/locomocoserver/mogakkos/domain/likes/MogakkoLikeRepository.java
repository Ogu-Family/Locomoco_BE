package org.prgms.locomocoserver.mogakkos.domain.likes;

import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoLikeRepository extends JpaRepository<MogakkoLike, Long> {
    Optional<MogakkoLike> findByMogakkoAndUser(Mogakko mogakko, User user);
    boolean existsByMogakkoAndUser(Mogakko mogakko, User user);
    List<MogakkoLike> findAllByUser(User user); // TODO: 페이징 처리
    @Query("SELECT COUNT(ml.id) FROM MogakkoLike ml INNER JOIN Mogakko m "
        + "ON ml.user = :user AND m.deletedAt IS NULL AND ml.mogakko = m")
    long countByUser(User user);
}
