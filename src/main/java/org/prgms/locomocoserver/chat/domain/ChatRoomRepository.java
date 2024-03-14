package org.prgms.locomocoserver.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByIdAndDeletedAtIsNull(Long id);

    @Query(value = "SELECT * FROM chat_rooms cr JOIN users_chat_room_list ucr ON cr.id = ucr.chat_room_list_id WHERE ucr.user_id = :userId AND cr.id < :cursorId ORDER BY cr.id DESC limit :pageSize",
            nativeQuery = true)
    List<ChatRoom> findByParticipantsId(Long userId, Long cursorId, int pageSize);
    Optional<ChatRoom> findByMogakkoId(Long mogakkoId);
}
