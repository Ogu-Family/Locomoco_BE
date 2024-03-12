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
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final StompChatService stompChatService;

    @Transactional
    public void enterChatRoom(ChatEnterRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        if (!isParticipantExist(chatRoom, requestDto.participant())) {
            ChatMessageDto chatMessageDto = saveEnterMessage(requestDto);
            chatRoom.addParticipant(requestDto.participant());
            stompChatService.sendToSubscribers(chatMessageDto);
        }
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
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found chatRoomId: " + id));
    }

    private boolean isParticipantExist(ChatRoom chatRoom, User user) {
        return chatRoom.getParticipants().stream()
                .anyMatch(participant -> participant.getId().equals(user.getId()));
    }

    private ChatMessage toEnterMessage(ChatRoom chatRoom, User participant) {
        return ChatMessage.builder().chatRoom(chatRoom).sender(
            participant).content(participant.getNickname() + "님이 입장하셨습니다.").build();
    }

    private ChatMessage getLastMessage(Long roomId) {
        return chatMessageRepository.findLastMessageByRoomId(roomId);
    }

    public ChatRoom createChatRoom(ChatCreateRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.save(requestDto.toChatRoomEntity());

        return chatRoom;
    }
}
