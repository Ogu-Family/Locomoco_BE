package org.prgms.locomocoserver.chat.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomMongoRepository extends MongoRepository<ChatRoomMongo, String> {
}
