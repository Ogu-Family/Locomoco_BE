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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MogakkoService mogakkoService;
    private final UserService userService;

    public ChatRoomDto createChatRoom(ChatMessageRequestDto messageRequestDto) {
        User loginUser = userService.getById(messageRequestDto.senderId());
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(messageRequestDto.mogakkoId());

        ChatRoom chatRoom = chatRoomRepository.save(messageRequestDto.toChatRoomEntity(mogakko, loginUser));
        chatMessageRepository.save(messageRequestDto.toChatMessageEntity());
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

    public ChatMessageDto saveChatMessage(ChatMessageRequestDto messageDto) {
        ChatMessage chatMessage = chatMessageRepository.save(messageDto.toChatMessageEntity());
        return new ChatMessageDto(chatMessage.getChatRoomId(), chatMessage.getSenderId(), chatMessage.getContent());
    }

    public List<ChatRoomDto> getAllChatRoom(Long userId, String cursor, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        if (cursor == null) {
            Page<ChatRoom> page = chatRoomRepository.findByCreatorIdAndDeletedAtIsNullOrderByUpdatedAtDesc(userId, pageable);
            pageable = page.nextPageable();
        }
        List<ChatRoomDto> chatRoomDtos = chatRoomRepository.findByCreatorIdAndDeletedAtIsNullOrderByUpdatedAtDesc(userId, pageable)
                .map(chatRoom -> ChatRoomDto.create(chatRoom))
                .stream().toList();
        return chatRoomDtos;
    }

    public List<ChatMessageDto> getAllChatMessages(Long roomId, String cursor, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        if (cursor == null) {
            Page<ChatMessage> page = chatMessageRepository.findByChatRoomIdAndDeletedAtIsNullOrderByCreatedAtDesc(roomId, pageable);
            pageable = page.nextPageable();
        }
        List<ChatMessageDto> chatMessageDtos = chatMessageRepository.findByChatRoomIdAndDeletedAtIsNullOrderByCreatedAtDesc(roomId, pageable)
              .map(chatMessage -> ChatMessageDto.of(chatMessage))
              .stream().toList();
        return chatMessageDtos;
    }

    public ChatRoom getById(Long id) {
        return chatRoomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found chatRoomId: " + id));
    }
}
