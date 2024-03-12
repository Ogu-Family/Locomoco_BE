package org.prgms.locomocoserver.chat.application;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatCreateRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MogakkoService mogakkoService;
    private final UserService userService;
    private final StompChatService stompChatService;

    @Transactional
    public ChatRoomDto enterChatRoom(ChatMessageRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        if (!isParticipantExist(chatRoom, requestDto.senderId())) {
            ChatMessageDto chatMessageDto = saveEnterMessage(requestDto);
            addParticipant(chatRoom, requestDto.senderId());
            stompChatService.sendToSubscribers(chatMessageDto);
        }

        return ChatRoomDto.of(chatRoom, ChatMessageDto.of(getLastMessage(chatRoom.getId())));
    }

    @Transactional
    public ChatMessageDto saveChatMessage(ChatMessageRequestDto requestDto) {
        User sender = userService.getById(requestDto.senderId());
        ChatRoom chatRoom = getById(requestDto.chatRoomId()); // updatedAt 갱신

        ChatMessage chatMessage = chatMessageRepository.save(requestDto.toChatMessageEntity(sender, chatRoom));
        chatRoom.updateUpdatedAt();
        return ChatMessageDto.of(chatMessage);
    }

    @Transactional
    public ChatMessageDto saveEnterMessage(ChatMessageRequestDto requestDto) {
        User sender = userService.getById(requestDto.senderId());
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        ChatMessage chatMessage = chatMessageRepository.save(requestDto.toEnterMessageEntity(sender, chatRoom));
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
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found chatRoomId: " + id));
    }

    private boolean isParticipantExist(ChatRoom chatRoom, Long userId) {
        return chatRoom.getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(userId));
    }

    private void addParticipant(ChatRoom existingRoom, Long participantId) {
        User newUser = userService.getById(participantId);
        existingRoom.addParticipant(newUser);
    }

    private ChatMessage getLastMessage(Long roomId) {
        return chatMessageRepository.findLastMessageByRoomId(roomId);
    }

    public ChatRoom createChatRoom(ChatCreateRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.save(requestDto.toChatRoomEntity());

        return chatRoom;
    }
}
