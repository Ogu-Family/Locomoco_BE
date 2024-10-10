package org.prgms.locomocoserver.chat.domain.mongo;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatMessageMongoCustomRepositoryImpl implements ChatMessageMongoCustomRepository {

    private static final String BASE_CHATROOM_NAME = "chat_messages_";
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatMessageMongo> findLatestMessageByRoomId(Long roomId) {
        String collectionName = getChatRoomName(roomId);
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "_id")).limit(1);

        ChatMessageMongo lastMessage = mongoTemplate.findOne(query, ChatMessageMongo.class, collectionName);
        return Optional.of(lastMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageMongo> findAllChatMessagesByRoomId(Long roomId, String cursorValue, int pageSize) {
        String collectionName = getChatRoomName(roomId);
        Query query = new Query();

        if (cursorValue != null && !cursorValue.trim().isEmpty() && !"null".equals(cursorValue)) {
            ObjectId cursorObjectId = new ObjectId(cursorValue);
            query.addCriteria(Criteria.where("_id").lt(cursorObjectId));
        }
        query.with(Sort.by(Sort.Direction.DESC, "_id")).limit(pageSize);

        try {
            return mongoTemplate.find(query, ChatMessageMongo.class, collectionName);
        } catch (Exception e) {
            throw new ChatException(ChatErrorType.CHAT_MESSAGE_NOT_FOUND, "채팅방에 메시지가 없습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteAllChatMessages(Long roomId) {
        String collectionName = getChatRoomName(roomId);
        try {
            mongoTemplate.dropCollection(collectionName);
        } catch (Exception e) {
            throw new RuntimeException("채팅방 삭제를 실패했습니다");
        }
    }

    private String getChatRoomName(Long roomId) {
        return BASE_CHATROOM_NAME + roomId;
    }
}
