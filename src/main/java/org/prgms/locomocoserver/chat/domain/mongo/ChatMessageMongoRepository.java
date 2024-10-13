package org.prgms.locomocoserver.chat.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageMongo, String> {

    Optional<ChatMessageMongo> findTopByChatRoomIdOrderByCreatedAtDesc(String chatRoomId);

    long countByChatRoomIdAndIdGreaterThan(String chatRoomId, String lastReadMsgId);

    void deleteByChatRoomId(String chatRoomId);
}
