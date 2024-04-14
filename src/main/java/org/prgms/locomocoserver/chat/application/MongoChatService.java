package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
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
    private final ChatRoomService chatRoomService;

    @Transactional
    public void createChatRoom(Long roomId) {
        String collectionName = BASE_CHATROOM_NAME + roomId;

        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }
    }

    public void saveEnterMessage(Long roomId, ChatMessageRequestDto message) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(message.senderId());
        mongoTemplate.save(toEnterMessage(roomId, participant), collectionName);
    }

    public void saveChatMessage(Long roomId, ChatMessageRequestDto message) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        User participant = userService.getById(message.senderId());
        ChatRoom chatRoom = chatRoomService.getById(roomId);
        mongoTemplate.save(message.toChatMessageEntity(participant, chatRoom, false), collectionName);
    }

    public List<ChatMessageDto> getAllChatMessages(Long roomId) {
        String collectionName = BASE_CHATROOM_NAME + roomId;
        return mongoTemplate.findAll(ChatMessageMongo.class, collectionName)
                .stream().map(chatMessageMongo -> ChatMessageDto.of(roomId, chatMessageMongo)).toList();
    }

    private ChatMessageMongo toEnterMessage(Long roomId, User participant) {
        return ChatMessageMongo.builder().senderId(participant.getId().toString()).senderId(participant.getId().toString())
                .senderImage(participant.getProfileImage().getPath()).createdAt(LocalDateTime.now())
                .message(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }

}
