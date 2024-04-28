package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.*;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongoRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatCreateRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final StompChatService stompChatService;
    private final MongoChatService mongoChatService;

    @Transactional
    public void enterChatRoom(ChatEnterRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        if (!isParticipantExist(chatRoom, requestDto.participant())) {
            ChatMessageDto chatMessageDto = saveEnterMessage(requestDto); // TODO : mysql chat
            mongoChatService.saveEnterMessage(requestDto.chatRoomId(), requestDto.participant().getId()); // TODO : mongo chat
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
        chatRoomRepository.save(chatRoom);
        chatMessageRepository.save(toEnterMessage(chatRoom, requestDto.creator()));  // TODO : mysql chat
        mongoChatService.saveEnterMessage(chatRoom.getId(), requestDto.creator().getId()); // TODO : mongo chat

        return chatRoom;
    }

    @Transactional
    public ChatMessageDto saveChatMessage(ChatMessageRequestDto requestDto) {
        User sender = userService.getById(requestDto.senderId());
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        ChatMessage chatMessage = chatMessageRepository.save(requestDto.toChatMessageEntity(sender, chatRoom, false));
        chatRoom.updateUpdatedAt();
        return ChatMessageDto.of(chatMessage);
    }

    @Transactional
    public ChatMessageDto saveEnterMessage(ChatEnterRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());
        User participant = requestDto.participant();

        ChatMessage chatMessage = chatMessageRepository.save(toEnterMessage(chatRoom, participant));
        chatRoom.updateUpdatedAt();
        return ChatMessageDto.of(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto> getAllChatRoom(Long userId, Long cursor, int pageSize) {
        if (cursor == null) cursor = Long.MAX_VALUE;
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsId(userId, cursor, pageSize);

        List<ChatRoomDto> chatRoomDtos = chatRooms.stream()
                .map(chatRoom -> ChatRoomDto.of(chatRoom, ChatMessageDto.of(getLastMessage(chatRoom.getId()))))
                .toList();
        return chatRoomDtos;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, Long cursor, int pageSize) {
        if (cursor == null) cursor = Long.MAX_VALUE;
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomIdAndIdGreaterThan(roomId, cursor, pageSize);

        List<ChatMessageDto> chatMessageDtos = chatMessages.stream()
                .map(ChatMessageDto::of)
                .collect(Collectors.toList());
        Collections.reverse(chatMessageDtos);

        return chatMessageDtos;
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
        chatMessageRepository.findAllByChatRoom(chatRoom).forEach(chatMessage -> chatMessage.delete());
        // 채팅방 삭제
        chatRoom.delete();
    }

    private boolean isParticipantExist(ChatRoom chatRoom, User user) {
        return chatRoom.getChatParticipants().stream()
                .anyMatch(chatParticipant -> chatParticipant.getUser().getId().equals(user.getId()));
    }

    private ChatMessage toEnterMessage(ChatRoom chatRoom, User participant) {
        return ChatMessage.builder().chatRoom(chatRoom).sender(
                participant).content(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }

    private ChatMessage getLastMessage(Long roomId) {
        return chatMessageRepository.findLastMessageByRoomId(roomId);
    }
}
