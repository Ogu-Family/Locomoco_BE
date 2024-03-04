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

    @Transactional
    public ChatRoomDto enterChatRoom(ChatMessageRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(requestDto.chatRoomId())
                .orElseGet(() -> createChatRoom(requestDto));

        if (!isParticipantExist(chatRoom, requestDto.senderId())) {
            addParticipant(chatRoom, requestDto.senderId());
        }

        return ChatRoomDto.of(chatRoom, ChatMessageDto.of(getLastMessage(chatRoom.getId())));
    }

    @Transactional
    public ChatMessageDto saveChatMessage(ChatMessageRequestDto messageDto) {
        User sender = userService.getById(messageDto.senderId());
        ChatRoom chatRoom = getById(messageDto.chatRoomId()); // updatedAt 갱신

        ChatMessage chatMessage = chatMessageRepository.save(messageDto.toChatMessageEntity(sender, chatRoom));
        chatRoom.updateUpdatedAt();
        return ChatMessageDto.of(chatMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto> getAllChatRoom(Long userId, Long cursor, int pageSize) {
        if (cursor == null) cursor = 0L;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<ChatRoom> page = chatRoomRepository.findByParticipantsId(userId, cursor, pageable);

        List<ChatRoomDto> chatRoomDtos = page.map(chatRoom -> ChatRoomDto.of(chatRoom, ChatMessageDto.of(getLastMessage(chatRoom.getId())))).stream().toList();

        return chatRoomDtos;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, Long cursor, int pageSize) {
        if (cursor == null) cursor = 0L;
        Pageable pageable = PageRequest.of(0, pageSize);
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

    private ChatMessage getLastMessage(Long roomId) {
        return chatMessageRepository.findLastMessageByRoomId(roomId);
    }

    private void addParticipant(ChatRoom existingRoom, Long participantId) {
        User newUser = userService.getById(participantId);
        existingRoom.addParticipant(newUser);
    }

    private ChatRoom createChatRoom(ChatMessageRequestDto messageRequestDto) {
        User loginUser = userService.getById(messageRequestDto.senderId());
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(messageRequestDto.mogakkoId());

        ChatRoom chatRoom = chatRoomRepository.save(messageRequestDto.toChatRoomEntity(mogakko, loginUser));
        chatMessageRepository.save(messageRequestDto.toChatMessageEntity(loginUser, chatRoom));
        return chatRoom;
    }
}
