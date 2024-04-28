package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MongoChatService {

    private static final String BASE_CHATROOM_NAME = "chat_messages_";
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void createChatRoom(Long roomId) {
        String collectionName = BASE_CHATROOM_NAME + roomId;

        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }
    }

    public ChatMessageDto saveEnterMessage(Long roomId, Long senderId) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(senderId);
        ChatMessageMongo chatMessageMongo = mongoTemplate.save(toEnterMessage(participant), collectionName);

        return ChatMessageDto.of(roomId, chatMessageMongo);
    }

    public void saveChatMessage(Long roomId, ChatMessageRequestDto message) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(message.senderId());
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
        mongoTemplate.save(message.toChatMessageMongo(participant, chatRoom, false), collectionName);
    }

    public List<ChatMessageDto> getAllChatMessages(Long roomId) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        return mongoTemplate.findAll(ChatMessageMongo.class, collectionName)
                .stream().map(chatMessageMongo -> ChatMessageDto.of(roomId, chatMessageMongo)).toList();
    }

    public String getChatRoomName(Long roomId) {
        return BASE_CHATROOM_NAME + roomId;
    }

    private ChatMessageMongo toEnterMessage(User participant) {
        return ChatMessageMongo.builder().senderId(participant.getId().toString())
                .senderImage(participant.getProfileImage().getPath()).createdAt(LocalDateTime.now())
                .message(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }
}
