package org.prgms.locomocoserver.mogakkos.domain.participants;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByMogakkoIdAndUserId(Long mogakkoId, Long userId);
}
