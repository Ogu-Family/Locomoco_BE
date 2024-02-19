package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.CreateChatRoomRequest;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MogakkoService mogakkoService;
    private final UserService userService;

    public ChatRoomDto createChatRoom(ChatMessageDto messageDto) {
        User loginUser = userService.getById(messageDto.senderId());
        Mogakko mogakko = mogakkoService.getById(messageDto.mogakkoId());

        ChatRoom chatRoom = chatRoomRepository.save(messageDto.toChatRoomEntity(mogakko, loginUser));
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }

    public ChatRoom getById(Long id) {
        return chatRoomRepository.findByIdAndDeletedAtIsNull(id)
             .orElseThrow(() -> new IllegalArgumentException("ChatRoom Not Found chatRoomId: " + id));
    }
}
