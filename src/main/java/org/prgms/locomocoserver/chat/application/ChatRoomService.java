package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatActivity;
import org.prgms.locomocoserver.chat.domain.mongo.ChatActivityRepository;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongoCustomRepository;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatParticipantCustomRepository;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatRoomCustomRepository;
import org.prgms.locomocoserver.chat.dto.*;
import org.prgms.locomocoserver.chat.dto.request.ChatCreateRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.querydsl.UserCustomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomCustomRepository chatRoomCustomRepository;
    private final ChatParticipantCustomRepository chatParticipantCustomRepository;
    private final UserCustomRepository userCustomRepository;
    private final ChatMessageMongoCustomRepository chatMessageMongoCustomRepository;
    private final ChatActivityRepository chatActivityRepository;

    private final StompChatService stompChatService;
    private final ChatMessagePolicy chatMessagePolicy;

    @Transactional
    public void enterChatRoom(ChatEnterRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        if (!isParticipantExist(chatRoom, requestDto.participant())) {
            ChatMessageDto chatMessageDto = saveEnterMessage(requestDto);
            ChatParticipant chatParticipant = chatParticipantCustomRepository.save(ChatParticipant.builder().user(requestDto.participant())
                    .chatRoom(chatRoom).build()).orElseThrow(() -> new RuntimeException("채팅방 참여에 실패했습니다."));

            chatActivityRepository.save(ChatActivity.builder().chatRoomId(requestDto.chatRoomId().toString())
                    .userId(requestDto.participant().getId().toString()).build());

            chatRoom.addChatParticipant(chatParticipant);
            stompChatService.sendToSubscribers(chatMessageDto);
        }
    }

    @Transactional
    public ChatRoom createChatRoom(ChatCreateRequestDto requestDto) {
        ChatRoom chatRoom = requestDto.toChatRoomEntity();
        ChatParticipant chatParticipant = chatParticipantCustomRepository.save(ChatParticipant.builder().user(requestDto.creator())
                .chatRoom(chatRoom).build()).orElseThrow(() -> new RuntimeException("채팅방 참여에 실패했습니다."));

        chatRoom.addChatParticipant(chatParticipant);
        chatRoomRepository.save(chatRoom); // mysql chat room create
        chatActivityRepository.save(ChatActivity.builder()
                .chatRoomId(chatRoom.getId().toString()).userId(requestDto.creator().getId().toString()).build());

        chatMessagePolicy.saveEnterMessage(chatRoom.getId(), chatParticipant.getUser());

        return chatRoom;
    }

    @Transactional
    public ChatMessageDto saveChatMessage(ChatMessageRequestDto requestDto) {
        ChatMessageDto chatMessageDto = chatMessagePolicy.saveChatMessage(requestDto.chatRoomId(), requestDto);

        return chatMessageDto;
    }

    @Transactional
    public ChatMessageDto saveChatMessageWithImage(Long roomId, List<String> imageUrls, ChatMessageRequestDto requestDto) {
        return chatMessagePolicy.saveChatMessageWithImage(roomId, imageUrls, requestDto);
    }

    @Transactional
    public ChatMessageDto saveEnterMessage(ChatEnterRequestDto requestDto) {
        return chatMessagePolicy.saveEnterMessage(requestDto.chatRoomId(), requestDto.participant());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto> getAllChatRoom(Long userId, String cursor, int pageSize) {
        if (cursor == null) cursor = LocalDateTime.now().toString();

        List<ChatRoom> chatRooms = chatRoomCustomRepository.findByParticipantsId(userId, cursor, pageSize);

        List<String> chatRoomIds = createChatRoomIds(chatRooms);
        log.info("START findLastMessagesAndUnReadMsgCount");
        List<ChatActivityDto> lastMessages = chatMessageMongoCustomRepository.findLastMessagesAndUnReadMsgCount(userId.toString(), chatRoomIds);

        Map<Long, ChatActivityDto> lastMsgMongoMap = lastMessages.stream()
                .collect(Collectors.toMap(
                        dto -> Long.parseLong(dto.chatRoomId()),
                        dto -> dto
                ));
        log.info(lastMsgMongoMap.toString());

        List<Long> userIds = createUserIds(lastMessages);
        log.info("START findByIdIn");
        Map<Long, User> userMap = userCustomRepository.findAllWithImageByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        log.info(userMap.toString());

        return chatRooms.stream()
                .map(chatRoom -> {
                    ChatActivityDto dto = lastMsgMongoMap.get(chatRoom.getId());
                    ChatMessageBriefDto lastMessageDto = ChatMessageBriefDto.of(dto.chatRoomId(), dto.chatMessageId().toString(), dto.senderId(), dto.message(), dto.createdAt());
                    User user = userMap.get(Long.parseLong(dto.senderId()));
                    ChatUserInfo chatUserInfo = user.getDeletedAt() == null ? ChatUserInfo.of(user) : ChatUserInfo.deletedUser(user.getId());

                    return ChatRoomDto.of(chatRoom, Integer.parseInt(dto.unReadMsgCnt()), lastMessageDto, chatUserInfo);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(User user, Long roomId, String cursor, int pageSize) {
        ChatRoom chatRoom = getById(roomId);
        if (!isParticipantExist(chatRoom, user)) {
            throw new ChatException(ChatErrorType.CHAT_PARTICIPANT_NOT_FOUND);
        }
        return chatMessagePolicy.getAllChatMessages(roomId, cursor, pageSize);
    }

    @Transactional(readOnly = true)
    public ChatRoom getById(Long id) {
        return chatRoomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Long getChatRoomIdByMogakkoId(Long mogakkoId) {
        ChatRoom chatRoom = chatRoomRepository.findByMogakkoId(mogakkoId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND, "해당 모각코의 채팅방이 존재하지 않습니다. : mogakkoId[" + mogakkoId + "]"));
        return chatRoom.getId();
    }

    @Transactional
    public void leave(ChatRoom chatRoom, Long userId) {
        chatParticipantCustomRepository.deleteByChatRoomIdAndUserId(chatRoom.getId(), userId);
    }

    @Transactional
    public void delete(ChatRoom chatRoom) {
        // 채팅방 참여자 목록 soft delete & 채팅방 메시지 보존
        chatParticipantCustomRepository.softDeleteParticipantsByRoomId(chatRoom.getId());
        // 채팅방 삭제
        chatRoom.delete();
    }

    private boolean isParticipantExist(ChatRoom chatRoom, User user) {
        return chatRoom.getChatParticipants().stream()
                .anyMatch(chatParticipant -> chatParticipant.getUser().getId().equals(user.getId()));
    }

    private List<String> createChatRoomIds(List<ChatRoom> chatRooms) {
        return chatRooms.stream()
                .map(chatRoom -> chatRoom.getId().toString()).collect(Collectors.toList());
    }

    private List<Long> createUserIds(List<ChatActivityDto> lastMessages) {
        return lastMessages.stream()
                .map(dao -> Long.valueOf(dao.senderId()))
                .distinct()
                .collect(Collectors.toList());
    }
}
