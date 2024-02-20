package org.prgms.locomocoserver.chat.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdAndDeletedAtIsNull(Long id);
    Page<ChatRoom> findByCreatorIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Long userId, Pageable pageable);
}
