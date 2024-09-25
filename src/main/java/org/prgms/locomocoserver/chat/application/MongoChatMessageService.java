package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatUserInfo;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.chat.querydsl.ChatRoomCustomRepository;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.exception.UserException;
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
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class MongoChatMessageService implements ChatMessagePolicy {

    private static final String BASE_CHATROOM_NAME = "chat_messages_";
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomCustomRepository chatRoomCustomRepository;

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

        return ChatMessageDto.of(roomId, chatMessageMongo, ChatUserInfo.of(sender));
    }

    @Transactional
    public ChatMessageDto saveChatMessage(Long roomId, ChatMessageRequestDto message) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(message.senderId());
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
        ChatMessageMongo chatMessageMongo = mongoTemplate.save(message.toChatMessageMongo(false, null), collectionName);

        return ChatMessageDto.of(roomId, chatMessageMongo, ChatUserInfo.of(participant));
    }

    @Override
    public ChatMessageDto saveChatMessageWithImage(Long roomId, List<String> imageUrls, ChatMessageRequestDto request) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(request.senderId());

        chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
        ChatMessageMongo chatMessageMongo = mongoTemplate.save(request.toChatMessageMongo(false, imageUrls), collectionName);

        return ChatMessageDto.of(roomId, imageUrls, chatMessageMongo, ChatUserInfo.of(participant));
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        String collectionName = getChatRoomName(roomId);
        Query query = new Query();

        if (!"null".equals(cursorValue)) {
            ObjectId cursorObjectId = new ObjectId(cursorValue);
            query.addCriteria(Criteria.where("_id").lt(cursorObjectId));
        }

        query.with(Sort.by(Sort.Direction.DESC, "_id")).limit(pageSize);
        List<ChatMessageMongo> chatMessages = mongoTemplate.find(query, ChatMessageMongo.class, collectionName);

        List<User> participants = chatRoomCustomRepository.findParticipantsByRoomId(roomId);
        Map<Long, ChatUserInfo> userMap = participants.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user.isDeleted()
                                ? ChatUserInfo.deletedUser(user.getId())
                                : ChatUserInfo.of(user)
                ));

        List<ChatMessageDto> chatMessageDtos = chatMessages.stream()
                .map(chatMessageMongo -> ChatMessageDto.of(roomId, chatMessageMongo, userMap.get(Long.parseLong(chatMessageMongo.getSenderId()))))
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

        if (lastMessage == null) return null;

        ChatUserInfo chatUserInfo = null;
        try {
            User user = userService.getById(Long.parseLong(lastMessage.getSenderId()));
            chatUserInfo = ChatUserInfo.of(user);
        } catch (UserException e) {
            chatUserInfo = ChatUserInfo.deletedUser(Long.parseLong(lastMessage.getSenderId()));
        }

        return ChatMessageDto.of(roomId, lastMessage, chatUserInfo);
    }

    public String getChatRoomName(Long roomId) {
        return BASE_CHATROOM_NAME + roomId;
    }

    private ChatMessageMongo toEnterMessage(User participant) {
        return ChatMessageMongo.builder().senderId(participant.getId().toString()).createdAt(LocalDateTime.now())
                .message(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }
}
