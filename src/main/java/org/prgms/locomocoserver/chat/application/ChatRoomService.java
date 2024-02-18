package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.CreateChatRoomRequest;
import org.prgms.locomocoserver.mogakkos.application.MogakkoService;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MogakkoService mogakkoService;

    public ChatRoomDto createChatRoom(CreateChatRoomRequest request, User loginUser) {
        Mogakko mogakko = mogakkoService.getById(request.mogakkoId());

        ChatRoom chatRoom = chatRoomRepository.save(request.toEntity(mogakko, loginUser));
        return new ChatRoomDto(chatRoom.getId(), chatRoom.getName());
    }
}
