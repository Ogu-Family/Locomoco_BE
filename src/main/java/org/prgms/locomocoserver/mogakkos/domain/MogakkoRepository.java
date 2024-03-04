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
    @Query(value = "SELECT m.* FROM mogakko m "
        + "INNER JOIN users u ON m.id > :cursor AND m.deleted_at IS NULL AND m.creator_id = u.id "
        + "INNER JOIN locations l ON l.mogakko_id = m.id "
        + "INNER JOIN mogakko_tags mt ON mt.tag_id IN :tagIds AND mt.mogakko_id = m.id "
        + "WHERE (m.title LIKE %:searchVal% OR m.content LIKE %:searchVal% OR u.nickname LIKE %:searchVal% OR l.city LIKE %:searchVal%) "
        + "GROUP BY m.id HAVING count(m.id) = :tagSize "
        + "LIMIT 20", nativeQuery = true)
    List<Mogakko> findAllByFilter(Long cursor, String searchVal, List<Long> tagIds, int tagSize); // TODO: 테스트 필요
    @Query(value = "SELECT DISTINCT m.* FROM mogakko m "
        + "INNER JOIN users u ON m.id > :cursor AND m.deleted_at IS NULL AND m.creator_id = u.id "
        + "INNER JOIN locations l ON l.mogakko_id = m.id "
        + "WHERE (m.title LIKE %:searchVal% OR m.content LIKE %:searchVal% OR u.nickname LIKE %:searchVal% OR l.city LIKE %:searchVal%) "
        + "LIMIT 20", nativeQuery = true)
    List<Mogakko> findAllByFilter(Long cursor, String searchVal); // TODO: 테스트 필요
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime >= :now AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findOngoingMogakkosByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime < :now AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findCompletedMogakkosByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}
