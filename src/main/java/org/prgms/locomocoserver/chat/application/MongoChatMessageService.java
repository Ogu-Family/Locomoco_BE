package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class MongoChatMessageService implements ChatMessagePolicy {

    private static final String BASE_CHATROOM_NAME = "chat_messages_";
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoom createChatRoom(Long roomId) {
        String collectionName = BASE_CHATROOM_NAME + roomId;

        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }

        return chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
    }

    @Transactional
    public ChatMessageDto saveEnterMessage(Long roomId, User sender) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        ChatMessageMongo chatMessageMongo = mongoTemplate.save(toEnterMessage(sender), collectionName);

        return ChatMessageDto.of(roomId, chatMessageMongo);
    }

    @Transactional
    public ChatMessageDto saveChatMessage(Long roomId, ChatMessageRequestDto message) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(message.senderId());
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
        ChatMessageMongo chatMessageMongo = mongoTemplate.save(message.toChatMessageMongo(participant, chatRoom, false), collectionName);

        return ChatMessageDto.of(roomId, chatMessageMongo);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        String collectionName = getChatRoomName(roomId);
        Query query = new Query();

        if (!"null".equals(cursorValue)) {
            ObjectId cursorObjectId = new ObjectId(cursorValue);
            query.addCriteria(Criteria.where("_id").gt(cursorObjectId));
        }


        query.with(Sort.by(Sort.Direction.DESC, "_id")).limit(pageSize);
        List<ChatMessageMongo> chatMessages = mongoTemplate.find(query, ChatMessageMongo.class, collectionName);

        List<ChatMessageDto> chatMessageDtos = chatMessages.stream()
                .map(chatMessageMongo -> ChatMessageDto.of(roomId, chatMessageMongo))
                .collect(Collectors.toList());
        Collections.reverse(chatMessageDtos);

        return chatMessageDtos;
    }

    @Transactional
    public void deleteChatMessages(ChatRoom chatRoom) {
        String collectionName = getChatRoomName(chatRoom.getId());
        mongoTemplate.dropCollection(collectionName);
    }

    @Transactional(readOnly = true)
    public ChatMessageDto getLastChatMessage(Long roomId) {
        String collectionName = getChatRoomName(roomId);

        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "_id")).limit(1);
        ChatMessageMongo lastMessage = mongoTemplate.findOne(query, ChatMessageMongo.class, collectionName);

        if(lastMessage == null) {
            throw new ChatException(ChatErrorType.CHAT_MESSAGE_NOT_FOUND, "채팅방 마지막 메시지를 조회할 수 없습니다. 채팅방번호 : " + roomId);
        }

        return ChatMessageDto.of(roomId, lastMessage);
    }

    public String getChatRoomName(Long roomId) {
        return BASE_CHATROOM_NAME + roomId;
    }

    private ChatMessageMongo toEnterMessage(User participant) {
        String profileImage = participant.getProfileImage() == null ? null : participant.getProfileImage().getPath();
        return ChatMessageMongo.builder().senderId(participant.getId().toString())
                .senderImage(profileImage).createdAt(LocalDateTime.now())
                .message(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }
}
