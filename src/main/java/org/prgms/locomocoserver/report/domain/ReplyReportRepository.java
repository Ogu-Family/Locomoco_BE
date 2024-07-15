package org.prgms.locomocoserver.report.domain;

import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyReportRepository extends JpaRepository<ReplyReport, Id> {

}
