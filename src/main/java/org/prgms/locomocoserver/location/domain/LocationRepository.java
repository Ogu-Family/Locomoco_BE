package org.prgms.locomocoserver.location.domain;

import java.util.Optional;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByMogakkoAndDeletedAtIsNull(Mogakko mogakko);
}
