package org.prgms.locomocoserver.mogakkos.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {
    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);
    @Query(value = "SELECT m.* FROM mogakko m JOIN locations l ON l.mogakko_id = m.id AND l.city LIKE :city% WHERE m.id > :cursor LIMIT 20", nativeQuery = true)
    List<Mogakko> findAll(Long cursor, String city);
}
