package org.prgms.locomocoserver.mogakkos.domain.location;

import java.util.List;
import java.util.Optional;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoLocationRepository extends JpaRepository<MogakkoLocation, Long> {
    Optional<MogakkoLocation> findByMogakkoAndDeletedAtIsNull(Mogakko mogakko);
    Optional<MogakkoLocation> findByMogakko(Mogakko mogakko);
    @Query("SELECT l FROM MogakkoLocation l WHERE l.mogakko IN :mogakkos")
    List<MogakkoLocation> findAllByMogakkos(List<Mogakko> mogakkos);
}
