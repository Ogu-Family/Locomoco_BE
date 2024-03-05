package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(requestDto.chatRoomId())
                .orElseGet(() -> createChatRoom(requestDto));

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
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<ChatRoom> page = chatRoomRepository.findByParticipantsId(userId, cursor, pageable);

        List<ChatRoomDto> chatRoomDtos = page.map(chatRoom -> ChatRoomDto.of(chatRoom, ChatMessageDto.of(getLastMessage(chatRoom.getId())))).stream().toList();

        return chatRoomDtos;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, Long cursor, int pageSize) {
        if (cursor == null) cursor = Long.MAX_VALUE;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<ChatMessage> page = chatMessageRepository.findAllByChatRoomIdAndIdGreaterThan(roomId, cursor, pageable);

        List<ChatMessageDto> chatMessageDtos = page.getContent().stream()
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

    private ChatRoom createChatRoom(ChatMessageRequestDto messageRequestDto) {
        User loginUser = userService.getById(messageRequestDto.senderId());
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(messageRequestDto.mogakkoId());

        ChatRoom chatRoom = chatRoomRepository.save(messageRequestDto.toChatRoomEntity(mogakko, loginUser));
        chatMessageRepository.save(messageRequestDto.toChatMessageEntity(loginUser, chatRoom));
        return chatRoom;
    }
}
