package org.prgms.locomocoserver.mogakkos.domain.midpoint;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MidpointRepository extends JpaRepository<Midpoint, Long> {
    Optional<Midpoint> findByMogakkoId(Long mogakkoId);
}
