package org.prgms.locomocoserver.mogakkos.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {
    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);
}
