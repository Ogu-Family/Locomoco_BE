package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Id> {
    Optional<ReplyReport> findByIdAndDeletedAtIsNull(Long id);
    @Query(value = "SELECT * FROM  reply_reports r WHERE r.deleted_at IS NULL AND r.id > :cursor ORDER BY r.id limit :pageSize", nativeQuery = true)
    List<ReplyReport> findAllByDeletedAtIsNull(Long cursor, int pageSize);
}
