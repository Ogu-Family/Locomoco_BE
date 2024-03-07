package org.prgms.locomocoserver.report.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByIdAndDeletedAtIsNull(Long id);
}
