package org.prgms.locomocoserver.mogakkos.domain.participants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByMogakkoIdAndUserId(Long mogakkoId, Long userId);

    @Query("SELECT COUNT(p.id) FROM Participant p INNER JOIN Mogakko m "
        + "ON p.user = :user AND m.endTime >= :time AND p.mogakko = m AND m.deletedAt IS NULL")
    long countOngoingByUser(User user, LocalDateTime time);

    @Query("SELECT COUNT(p.id) FROM Participant p INNER JOIN Mogakko m "
        + "ON p.user = :user AND m.endTime < :time AND p.mogakko = m AND m.deletedAt IS NULL")
    long countCompleteByUser(User user, LocalDateTime time);

    List<Participant> findAllByMogakkoId(long mogakkoId);
}
