package org.prgms.locomocoserver.chat.domain.mongo;

import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.prgms.locomocoserver.chat.dao.ChatActivityDao;
import org.prgms.locomocoserver.chat.dao.ChatActivityRequestDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<ChatActivityDao> findLastMessagesAndUnReadMsgCount(List<ChatActivityRequestDao> chatRoomInfoDtos) {
        // 1. 채팅방 roomId 목록 생성
        List<String> chatRoomIds = chatRoomInfoDtos.stream()
                .map(ChatActivityRequestDao::chatRoomId)
                .collect(Collectors.toList());

        // 2. 채팅방 필터링 (roomId 기반)
        Criteria criteria = Criteria.where("chatRoomId").in(chatRoomIds);
        AggregationOperation matchChatRooms = Aggregation.match(criteria);

        // 3. 메시지 시간 순으로 정렬 (최신 메시지가 상단에 오도록)
        AggregationOperation sortMessagesByCreatedAtDesc = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));

        // 4. 각 채팅방별로 마지막 메시지를 그룹화하고 필요한 필드 선택
        AggregationOperation groupLastMessage = Aggregation.group("chatRoomId")
                .first("chatRoomId").as("chatRoomId")
                .first("senderId").as("senderId")
                .first("message").as("message")
                .first("createdAt").as("createdAt")
                .first("_id").as("chatMessageId");

        // 5. 필요 필드만 선택하는 project 단계 추가
        AggregationOperation projectFields = Aggregation.project()
                .and("chatRoomId").as("chatRoomId")
                .and("senderId").as("senderId")
                .and("message").as("message")
                .and("createdAt").as("createdAt")
                .and("chatMessageId").as("chatMessageId");

        // 6. Aggregation Pipeline 생성
        Aggregation aggregation = Aggregation.newAggregation(
                matchChatRooms,
                sortMessagesByCreatedAtDesc,
                groupLastMessage,
                projectFields
        );

        // 7. Aggregation 결과 실행
        List<ChatActivityDao> lastMessages = mongoTemplate.aggregate(aggregation, "chat_messages", ChatActivityDao.class)
                .getMappedResults();

        // 8. 읽지 않은 메시지 수 계산을 위한 roomId와 lastReadMsgId 맵 생성
        Map<String, ObjectId> lastReadMessageIdMap = chatRoomInfoDtos.stream()
                .filter(dao -> dao.lastReadMsgId() != null)
                .collect(Collectors.toMap(
                        ChatActivityRequestDao::chatRoomId,
                        ChatActivityRequestDao::lastReadMsgId
                ));

        // 9. unread 메시지 수를 가져오기 위한 쿼리 생성
        List<ChatActivityDao> unreadCounts = lastMessages.stream()
                .map(chatActivityDao -> {
                    String chatRoomId = chatActivityDao.chatRoomId();
                    ObjectId lastReadMsgId = lastReadMessageIdMap.getOrDefault(chatRoomId, null);

                    // 10. unread 메시지 수 계산 쿼리
                    long unreadCount = mongoTemplate.getCollection("chat_messages")
                            .countDocuments(Filters.and(
                                    Filters.eq("chatRoomId", chatRoomId),
                                    Filters.gt("_id", lastReadMsgId)
                            ));

                    // 11. 읽지 않은 메시지 수와 마지막 메시지 결합
                    return new ChatActivityDao(String.valueOf(unreadCount), chatActivityDao.chatRoomId(),
                            chatActivityDao.chatMessageId(), chatActivityDao.senderId(),
                            chatActivityDao.message(), chatActivityDao.createdAt());
                })
                .collect(Collectors.toList());

        // 12. 결과 반환
        return unreadCounts;
    }

}
