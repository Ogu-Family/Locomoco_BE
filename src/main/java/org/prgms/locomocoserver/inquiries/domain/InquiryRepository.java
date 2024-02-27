package org.prgms.locomocoserver.inquiries.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findByIdAndDeletedAtIsNull(Long id);
}
