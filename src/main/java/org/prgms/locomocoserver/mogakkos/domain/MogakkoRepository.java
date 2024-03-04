package org.prgms.locomocoserver.mogakkos.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {
    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);
    @Query(value = "SELECT m.* FROM mogakko m "
        + "JOIN locations l ON (m.id > :cursor AND m.deleted_at IS NULL AND l.mogakko_id = m.id AND l.city LIKE :city%) "
        + "LIMIT 20", nativeQuery = true)
    List<Mogakko> findAllByCity(Long cursor, String city);
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime >= :now AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findOngoingMogakkosByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime < :now AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findCompletedMogakkosByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}
