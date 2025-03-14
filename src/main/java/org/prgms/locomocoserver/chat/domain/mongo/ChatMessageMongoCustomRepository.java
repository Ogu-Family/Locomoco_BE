package org.prgms.locomocoserver.chat.domain.mongo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.prgms.locomocoserver.chat.dto.ChatActivityDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatMessageMongoCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Transactional(readOnly = true)
    public List<ChatMessageMongo> findAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        ObjectId cursor = cursorValue == null ? new ObjectId() : new ObjectId(cursorValue);
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.addCriteria(Criteria.where("chatRoomId").is(roomId.toString())
                .and("_id").lt(cursor));
        query.limit(pageSize);

        return mongoTemplate.find(query, ChatMessageMongo.class);
    }

    @Transactional(readOnly = true)
    public List<ChatActivityDto> findLastMessagesAndUnReadMsgCount(String userId, List<String> chatRoomIds) {
        // 1. 채팅방별 마지막 메시지 조회
        Map<String, ChatActivityDto> lastMsgMap = findLastMessages(chatRoomIds);
        if (lastMsgMap == null || lastMsgMap.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 채팅방별 lastReadMsgId 조회
        Map<String, ObjectId> lastReadMsgIdMap = fetchLastReadMsgIds(userId, chatRoomIds);
        log.info("findLastMessagesAndUnReadMsgCount - lastReadMsgIdMap : " + lastReadMsgIdMap);

        // 3. 읽지 않은 메시지 수 계산 기준 생성
        List<Criteria> criteriaList = createUnreadMessageCriteria(lastReadMsgIdMap);
        log.info("findLastMessagesAndUnReadMsgCount - criteriaList : " + criteriaList);

        // 4. 읽지 않은 메시지 수 집계
        List<ChatActivityDto> result = countUnreadMessages(criteriaList, lastMsgMap);
        log.info("findLastMessagesAndUnreadMsgCount - result : " + result);

        return result;
    }

    // 마지막 메시지 조회
    private Map<String, ChatActivityDto> findLastMessages(List<String> chatRoomIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chatRoomId").in(chatRoomIds)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")),
                Aggregation.group("chatRoomId")
                        .first("chatRoomId").as("chatRoomId")
                        .first("senderId").as("senderId")
                        .first("message").as("message")
                        .first("createdAt").as("createdAt")
                        .first("_id").as("chatMessageId"),
                Aggregation.project("chatRoomId", "senderId", "message", "createdAt", "chatMessageId")
        );

        List<ChatActivityDto> lastMessages = mongoTemplate.aggregate(aggregation, "chat_messages", ChatActivityDto.class)
                .getMappedResults();
        if (lastMessages == null || lastMessages.isEmpty()) {
            return new HashMap<>();
        }

        return lastMessages.stream()
                .collect(Collectors.toMap(ChatActivityDto::chatRoomId, chatActivityDto -> chatActivityDto));
    }

    // lastReadMsgId 조회
    private Map<String, ObjectId> fetchLastReadMsgIds(String userId, List<String> chatRoomIds) {
        Query query = new Query(Criteria.where("userId").is(userId).and("chatRoomId").in(chatRoomIds));
        query.fields().include("chatRoomId").include("lastReadMsgId");

        List<Document> results = mongoTemplate.find(query, Document.class, "chat_activity");
        if (results == null || results.isEmpty()) {
            return new HashMap<>();
        }

        return results.stream()
                .collect(Collectors.toMap(
                        doc -> doc.getString("chatRoomId"),
                        doc -> {
                            ObjectId lastReadMsgId = doc.getObjectId("lastReadMsgId");
                            return lastReadMsgId != null ? lastReadMsgId : new ObjectId();
                        }
                ));
    }

    // 읽지 않은 메시지 수 계산 기준 생성
    private List<Criteria> createUnreadMessageCriteria(Map<String, ObjectId> lastReadMsgIdMap) {
        return lastReadMsgIdMap.entrySet().stream()
                .map(entry -> {
                    return Criteria.where("chatRoomId").is(entry.getKey()).and("_id").gt(entry.getValue());
                })
                .collect(Collectors.toList());

    }

    // 읽지 않은 메시지 수 집계
    private List<ChatActivityDto> countUnreadMessages(List<Criteria> criteriaList, Map<String, ChatActivityDto> lastMsgMap) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().orOperator(criteriaList.toArray(new Criteria[0]))),
                Aggregation.group("chatRoomId").count().as("unreadCount"),
                Aggregation.project("unreadCount").and("chatRoomId").previousOperation()
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "chat_messages", Document.class);

        Map<String, Integer> unreadCountMap = results.getMappedResults().stream()
                .collect(Collectors.toMap(doc -> doc.getString("chatRoomId"), doc -> doc.getInteger("unreadCount")));

        return lastMsgMap.values().stream()
                .map(dto -> {
                    int unreadCount = unreadCountMap.getOrDefault(dto.chatRoomId(), 0); // 기본값 0 처리
                    return new ChatActivityDto(String.valueOf(unreadCount), dto.chatRoomId(),
                            dto.chatMessageId(), dto.senderId(), dto.message(), dto.createdAt());
                })
                .collect(Collectors.toList());
    }
}
