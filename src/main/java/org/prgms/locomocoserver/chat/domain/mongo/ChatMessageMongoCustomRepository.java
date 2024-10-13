package org.prgms.locomocoserver.chat.domain.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageMongoCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Transactional(readOnly = true)
    public List<ChatMessageMongo> findAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomId").is(roomId.toString())
                .and("createdAt").lt(LocalDateTime.parse(cursorValue)));
        query.limit(pageSize);

        return mongoTemplate.find(query, ChatMessageMongo.class);
    }
}
