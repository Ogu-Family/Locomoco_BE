package org.prgms.locomocoserver.chat.domain.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageMongo, String> {

    Optional<ChatMessageMongo> findTopByChatRoomIdOrderByCreatedAtDesc(String chatRoomId);

    List<ChatMessageMongo> findAllByChatRoomId(String chatRoomId);

    long countByChatRoomIdAndIdGreaterThan(String chatRoomId, ObjectId lastReadMsgId);

    void deleteByChatRoomId(String chatRoomId);
}
