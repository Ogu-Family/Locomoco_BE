package org.prgms.locomocoserver.replies.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndDeletedAtIsNotNull(Long id);
}
