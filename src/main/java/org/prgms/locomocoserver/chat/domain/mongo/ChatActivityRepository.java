package org.prgms.locomocoserver.chat.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatActivityRepository extends MongoRepository<ChatActivity, String> {
    Optional<ChatActivity> findByUserIdAndChatRoomId(String userId, String chatRoomId);
}
