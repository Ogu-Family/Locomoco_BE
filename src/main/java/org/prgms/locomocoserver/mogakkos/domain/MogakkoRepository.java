package org.prgms.locomocoserver.mogakkos.domain;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {

    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Mogakko m WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Mogakko> findByIdAndDeletedAtIsNullForUpdate(Long id); // 이 코드는 추후 따로 클래스 분리를 할 수도 있음

    @Query(value = "SELECT m.* "
        + "FROM mogakko m "
        + "JOIN locations l ON l.mogakko_id = m.id AND m.deadline > :searchTime "
            + "AND m.deleted_at IS NULL AND (l.city LIKE CONCAT(:city, '%') OR l.h_city LIKE CONCAT(:city, '%')) "
        + "LEFT JOIN mogakko_tags mt ON mt.tag_id IN :tagIds AND mt.mogakko_id = m.id "
        + "GROUP BY m.id "
        + "ORDER BY COUNT(mt.id) DESC, m.created_at DESC "
        + "LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<Mogakko> findAllByCity(Iterable<Long> tagIds, String city, int pageSize, LocalDateTime searchTime, Long offset); // 장소

    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime >= :searchTime AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findOngoingMogakkosByUser(User user, LocalDateTime searchTime);

    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime < :searchTime AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findCompletedMogakkosByUser(User user, LocalDateTime searchTime);

    @Modifying
    @Query("UPDATE Mogakko m SET m.views = m.views + 1 WHERE m = :mogakko")
    void increaseViews(Mogakko mogakko);
}
