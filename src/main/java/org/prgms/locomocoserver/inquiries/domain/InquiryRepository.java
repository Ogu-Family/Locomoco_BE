package org.prgms.locomocoserver.inquiries.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findByIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT i.* FROM inquiries i WHERE id < :cursor "
        + "ORDER BY i.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Inquiry> findAll(Long cursor, int pageSize);

    @Query(value = "SELECT i.* FROM inquiries i "
        + "INNER JOIN users u ON i.id < :cursor AND u.id = :userId AND i.user_id = u.id "
        + "ORDER BY i.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Inquiry> findAllByUser(Long cursor, Long userId, int pageSize);

    @Query(value = "SELECT i.* FROM inquiries i "
        + "INNER JOIN mogakko m ON i.id < :cursor AND m.id = :mogakkoId AND i.mogakko_id = m.id AND i.deleted_at IS NULL "
        + "ORDER BY i.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Inquiry> findAllByMogakko(Long cursor, Long mogakkoId, int pageSize);

    @Query(value = "SELECT i.* FROM inquiries i "
        + "INNER JOIN mogakko m ON i.id < :cursor AND m.id = :mogakkoId AND i.mogakko_id = m.id "
        + "INNER JOIN users u ON u.id = :userId AND i.user_id = u.id "
        + "ORDER BY i.created_at DESC "
        + "LIMIT :pageSize", nativeQuery = true)
    List<Inquiry> findAllByMogakkoAndUser(Long cursor, Long mogakkoId, Long userId, int pageSize);
}
