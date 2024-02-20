package org.prgms.locomocoserver.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdAndDeletedAtIsNull(Long id);
    Optional<ChatRoom> findAllByUserIdAndDeletedAtIsNull(Long userId);
}
