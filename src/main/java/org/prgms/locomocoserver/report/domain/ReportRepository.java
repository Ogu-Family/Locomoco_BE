package org.prgms.locomocoserver.report.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByIdAndDeletedAtIsNull(Long id);
    @Query(value = "SELECT * FROM  reports r WHERE r.deleted_at IS NULL AND r.id > :cursor ORDER BY r.id limit :pageSize", nativeQuery = true)
    List<Report> findAllByDeletedAtIsNull(Long cursor, int pageSize);
}
