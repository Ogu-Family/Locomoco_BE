package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.ChatParticipantRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatRoomCustomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatCreateRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final MongoChatMessageService mongoChatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomCustomRepository chatRoomCustomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatActivityService chatActivityService;

    private final StompChatService stompChatService;
    private final ChatMessagePolicy chatMessagePolicy;

    @Transactional
    public void enterChatRoom(ChatEnterRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        if (!isParticipantExist(chatRoom, requestDto.participant())) {
            ChatMessageDto chatMessageDto = saveEnterMessage(requestDto);
            chatMessagePolicy.saveEnterMessage(requestDto.chatRoomId(), requestDto.participant());
            ChatParticipant chatParticipant = chatParticipantRepository.save(ChatParticipant.builder().user(requestDto.participant())
                    .chatRoom(chatRoom).build());

            chatRoom.addChatParticipant(chatParticipant);
            stompChatService.sendToSubscribers(chatMessageDto);
        }
    }

    @Transactional
    public ChatRoom createChatRoom(ChatCreateRequestDto requestDto) {
        ChatRoom chatRoom = requestDto.toChatRoomEntity();
        ChatParticipant chatParticipant = chatParticipantRepository.save(ChatParticipant.builder().user(requestDto.creator())
                .chatRoom(chatRoom).build());

        chatRoom.addChatParticipant(chatParticipant);
        chatRoomRepository.save(chatRoom); // mysql chat room create
        mongoChatMessageService.createChatRoom(chatRoom.getId()); // mongo chat room create

        chatMessagePolicy.saveEnterMessage(chatRoom.getId(), chatParticipant.getUser());

        return chatRoom;
    }

    @Transactional
    public ChatMessageDto saveChatMessage(ChatMessageRequestDto requestDto) {
        return chatMessagePolicy.saveChatMessage(requestDto.chatRoomId(), requestDto);
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
    public List<ChatRoomDto> getAllChatRoom(Long userId, Long cursor, int pageSize) {
        if (cursor == null) cursor = Long.MAX_VALUE;
        List<ChatRoom> chatRooms = chatRoomCustomRepository.findByParticipantsId(userId, cursor, pageSize);

        List<ChatRoomDto> chatRoomDtos = chatRooms.stream()
                .map(chatRoom -> {
                    ChatMessageDto lastMessageDto = chatMessagePolicy.getLastChatMessage(chatRoom.getId());
                    ChatParticipant chatParticipant = getChatParticipant(chatRoom, userId);

                    int unReadMsgCnt = chatActivityService.unReadMessageCount(lastMessageDto.chatMessageId(), chatParticipant.getLastReadMessageId());
                    return ChatRoomDto.of(chatRoom, unReadMsgCnt, lastMessageDto);
                })
                .filter(Objects::nonNull) // null인 경우 제외
                .toList();
        return chatRoomDtos;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, String cursor, int pageSize) {
        String cursorValue = cursor == null ? "null" : cursor;
        return chatMessagePolicy.getAllChatMessages(roomId, cursorValue, pageSize);
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
        chatParticipantRepository.deleteByChatRoomIdAndUserId(chatRoom.getId(), userId);
    }

    @Transactional
    public void delete(ChatRoom chatRoom) {
        // 채팅방 참여자 목록 삭제
        chatParticipantRepository.deleteAllByChatRoom(chatRoom);
        // 채팅방 메시지 삭제
        chatMessagePolicy.deleteChatMessages(chatRoom);
        // 채팅방 삭제
        chatRoom.delete();
    }

    private boolean isParticipantExist(ChatRoom chatRoom, User user) {
        return chatRoom.getChatParticipants().stream()
                .anyMatch(chatParticipant -> chatParticipant.getUser().getId().equals(user.getId()));
    }

    private ChatParticipant getChatParticipant(ChatRoom chatRoom, Long userId) {
        return chatRoom.getChatParticipants().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
