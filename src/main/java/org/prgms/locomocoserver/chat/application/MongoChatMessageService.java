package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongo;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongoCustomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongoRepository;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatRoomCustomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatUserInfo;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.context.annotation.Primary;
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

    private final ChatMessageMongoRepository chatMessageMongoRepository;
    private final ChatMessageMongoCustomRepository chatMessageMongoCustomRepository;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomCustomRepository chatRoomCustomRepository;

    @Transactional
    public ChatMessageDto saveEnterMessage(Long roomId, User sender) {
        ChatMessageMongo chatMessageMongo = chatMessageMongoRepository.save(toEnterMessage(roomId, sender));

        return ChatMessageDto.of(roomId, chatMessageMongo, ChatUserInfo.of(sender));
    }

    @Transactional
    public ChatMessageDto saveChatMessage(Long roomId, ChatMessageRequestDto message) {
        User participant = userService.getById(message.senderId());

        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));

        ChatMessageMongo chatMessageMongo = chatMessageMongoRepository.save(message.toChatMessageMongo(roomId, false, null));
        chatRoom.updateUpdatedAt();

        return ChatMessageDto.of(roomId, chatMessageMongo, ChatUserInfo.of(participant));
    }

    @Transactional
    public ChatMessageDto saveChatMessageWithImage(Long roomId, List<String> imageUrls, ChatMessageRequestDto request) {
        User participant = userService.getById(request.senderId());

        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));

        ChatMessageMongo chatMessageMongo = chatMessageMongoRepository.save(request.toChatMessageMongo(roomId, false, imageUrls));
        chatRoom.updateUpdatedAt();

        return ChatMessageDto.of(roomId, imageUrls, chatMessageMongo, ChatUserInfo.of(participant));
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        List<ChatMessageMongo> chatMessages = chatMessageMongoCustomRepository.findAllChatMessages(roomId, cursorValue, pageSize);
        Map<Long, ChatUserInfo> userMap = getUserMap(roomId);

        return createChatMessageDtos(roomId, chatMessages, userMap);
    }

    @Transactional
    public void deleteChatMessages(ChatRoom chatRoom) {
        chatMessageMongoRepository.deleteByChatRoomId(chatRoom.getId().toString());
    }

    @Transactional(readOnly = true)
    public ChatMessageDto getLastChatMessage(Long roomId) {
        ChatMessageMongo lastMessage = chatMessageMongoRepository.findTopByChatRoomIdOrderByCreatedAtDesc(roomId.toString())
                .orElseThrow(() -> new ChatException(ChatErrorType.CHAT_MESSAGE_NOT_FOUND));

        ChatUserInfo chatUserInfo = getChatUserInfo(lastMessage.getSenderId());
        return ChatMessageDto.of(roomId, lastMessage, chatUserInfo);
    }

    private ChatMessageMongo toEnterMessage(Long roomId, User participant) {
        return ChatMessageMongo.builder().chatRoomId(roomId.toString()).senderId(participant.getId().toString()).createdAt(LocalDateTime.now())
                .message(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }

    private Map<Long, ChatUserInfo> getUserMap(Long roomId) {
        List<User> participants = chatRoomCustomRepository.findParticipantsByRoomId(roomId);
        return participants.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> ChatUserInfo.of(user)
                ));
    }

    private List<ChatMessageDto> createChatMessageDtos(Long roomId, List<ChatMessageMongo> chatMessages, Map<Long, ChatUserInfo> userMap) {
        List<ChatMessageDto> chatMessageDtos = chatMessages.stream()
                .map(chatMessageMongo -> {
                    Long senderId = Long.parseLong(chatMessageMongo.getSenderId());
                    ChatUserInfo userInfo = userMap.get(senderId);

                    // userInfo가 null일 경우 삭제된 사용자 정보 반환
                    if (userInfo == null) {
                        userInfo = ChatUserInfo.deletedUser(senderId);
                    }

                    return ChatMessageDto.of(roomId, chatMessageMongo, userInfo);
                })
                .collect(Collectors.toList());
        Collections.reverse(chatMessageDtos);
        return chatMessageDtos;
    }

    private ChatUserInfo getChatUserInfo(String senderId) {
        try {
            User user = userService.getById(Long.parseLong(senderId));
            return ChatUserInfo.of(user);
        } catch (UserException e) {
            return ChatUserInfo.deletedUser(Long.parseLong(senderId));
        }
    }
}
