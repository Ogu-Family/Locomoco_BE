package org.prgms.locomocoserver.location.domain;

import java.util.List;
import java.util.Optional;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByMogakkoAndDeletedAtIsNull(Mogakko mogakko);
    Optional<Location> findByMogakko(Mogakko mogakko);
    @Query("SELECT l FROM Location l WHERE l.mogakko IN :mogakkos")
    List<Location> findAllByMogakkos(List<Mogakko> mogakkos);
}
