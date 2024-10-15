package org.prgms.locomocoserver.chat.domain.mongo;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageMongoCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Transactional(readOnly = true)
    public List<ChatMessageMongo> findAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        ObjectId cursor = cursorValue == null ? new ObjectId(new Date(Long.MAX_VALUE)) : new ObjectId(cursorValue);
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.addCriteria(Criteria.where("chatRoomId").is(roomId.toString())
                .and("_id").lt(cursor));
        query.limit(pageSize);

        return mongoTemplate.find(query, ChatMessageMongo.class);
    }
}
