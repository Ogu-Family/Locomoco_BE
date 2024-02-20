package org.prgms.locomocoserver.review.domain.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewContentRepository extends JpaRepository<ReviewContent, Long> {
    List<ReviewContent> findAllByDeletedAtIsNull();
}
