package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MogakkoService mogakkoService;
    private final UserService userService;

    public ChatRoomDto createChatRoom(ChatMessageDto messageDto) {
        User loginUser = userService.getById(messageDto.senderId());
        Mogakko mogakko = mogakkoService.getByIdNotDeleted(messageDto.mogakkoId());

        ChatRoom chatRoom = chatRoomRepository.save(messageDto.toChatRoomEntity(mogakko, loginUser));
        chatMessageRepository.save(messageDto.toChatMessageEntity());
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

    public ChatMessageDto saveChatMessage(ChatMessageDto messageDto) {
        ChatMessage chatMessage = chatMessageRepository.save(messageDto.toChatMessageEntity());
        return new ChatMessageDto(chatMessage.getChatRoomId(), messageDto.mogakkoId(), chatMessage.getSenderId(), chatMessage.getContent());
    }

    public List<ChatRoomDto> getAllChatRoom(Long userId) {
        List<ChatRoomDto> chatRoomDtos = chatRoomRepository.findAllByUserIdAndDeletedAtIsNull(userId)
                .map(chatRoom -> ChatRoomDto.create(chatRoom))
                .stream().toList();
        return chatRoomDtos;
    }

    public ChatRoom getById(Long id) {
        return chatRoomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found chatRoomId: " + id));
    }
}
