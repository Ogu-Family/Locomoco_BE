package org.prgms.locomocoserver.mogakkos.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MogakkoRepository extends JpaRepository<Mogakko, Long> {

    Optional<Mogakko> findByIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT m.* FROM mogakko m "
        + "JOIN locations l ON (m.id < :cursor AND m.deadline > :now AND m.deleted_at IS NULL AND l.mogakko_id = m.id AND l.city LIKE :city%) "
        + "ORDER BY m.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Mogakko> findAllByCity(Long cursor, String city, int pageSize, LocalDateTime now);

    @Query(value = "SELECT m.* "
        + "FROM mogakko_tags mt "
        + "INNER JOIN mogakko m ON m.id < :cursor AND m.deadline > :now AND m.deleted_at IS NULL AND m.id = mt.mogakko_id "
        + "INNER JOIN locations l ON l.mogakko_id = m.id AND l.city LIKE :city% "
        + "WHERE mt.tag_id IN :tagIds "
        + "GROUP BY mt.mogakko_id HAVING COUNT(mt.mogakko_id) = :tagSize "
        + "ORDER BY m.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Mogakko> findAllByCity(Iterable<Long> tagIds, int tagSize, Long cursor, String city, int pageSize, LocalDateTime now);

    @Query(value = "SELECT m.* FROM mogakko m "
        + "INNER JOIN users u ON m.id < :cursor AND m.deleted_at IS NULL AND m.deadline > :now AND m.creator_id = u.id "
        + "INNER JOIN locations l ON l.mogakko_id = m.id "
        + "INNER JOIN mogakko_tags mt ON mt.tag_id IN :tagIds AND mt.mogakko_id = m.id "
        + "WHERE (m.title LIKE %:searchVal% OR m.content LIKE %:searchVal% OR u.nickname LIKE %:searchVal% OR l.city LIKE %:searchVal%) "
        + "GROUP BY m.id HAVING count(m.id) = :tagSize "
        + "ORDER BY m.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Mogakko> findAllByFilter(Long cursor, String searchVal, List<Long> tagIds, int tagSize, int pageSize, LocalDateTime now);

    @Query(value = "SELECT DISTINCT m.* FROM mogakko m "
        + "INNER JOIN users u ON m.id < :cursor AND m.deadline > :now AND m.deleted_at IS NULL AND m.creator_id = u.id "
        + "INNER JOIN locations l ON l.mogakko_id = m.id "
        + "WHERE (m.title LIKE %:searchVal% OR m.content LIKE %:searchVal% OR u.nickname LIKE %:searchVal% OR l.city LIKE %:searchVal%) "
        + "ORDER BY m.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Mogakko> findAllByFilter(Long cursor, String searchVal, int pageSize, LocalDateTime now);

    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime >= :now AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findOngoingMogakkosByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT m FROM Mogakko m JOIN m.participants p WHERE p.user = :user AND m.endTime < :now AND m.deletedAt IS NULL AND p.user.deletedAt IS NULL")
    List<Mogakko> findCompletedMogakkosByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE Mogakko m SET m.views = m.views + 1 WHERE m = :mogakko")
    void increaseViews(Mogakko mogakko);
}
