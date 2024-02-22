package org.prgms.locomocoserver.mogakkos.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {
    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user.id = :userId AND m.deadline >= :now AND m.deletedAt IS NULL")
    List<Mogakko> findOngoingMogakkosByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}
