package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Id> {
    Optional<ReplyReport> findById(Long id);
}
