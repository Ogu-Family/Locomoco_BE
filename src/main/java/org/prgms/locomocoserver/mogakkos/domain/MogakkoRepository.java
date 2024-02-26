package org.prgms.locomocoserver.mogakkos.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {
    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);
    @Query(value = "SELECT m.* FROM mogakko m "
        + "JOIN locations l ON (m.id > :cursor AND m.deleted_at IS NULL AND l.mogakko_id = m.id AND l.city LIKE :city%) "
        + "LIMIT 20", nativeQuery = true)
    List<Mogakko> findAll(Long cursor, String city);
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user.id = :userId AND m.deadline >= :now AND m.deletedAt IS NULL")
    List<Mogakko> findOngoingMogakkosByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user.id = :userId AND m.deadline < :now AND m.deletedAt IS NULL")
    List<Mogakko> findCompletedMogakkosByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
