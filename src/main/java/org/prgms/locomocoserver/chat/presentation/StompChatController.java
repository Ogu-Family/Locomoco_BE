package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;

    @MessageMapping(value = "/chats/enter")
    public void enter(ChatMessageRequestDto requestDto) {

        // 채팅방이 이미 존재하는지 확인
        ChatRoom existingRoom = chatRoomService.getById(requestDto.chatRoomId());

        // 채팅방이 존재하지 않으면 새로운 채팅방을 생성
        if (existingRoom == null) {
            ChatRoomDto newRoom = chatRoomService.createChatRoom(requestDto);
            requestDto = new ChatMessageRequestDto(newRoom.roomId(), requestDto.senderId(), requestDto.mogakkoId(), requestDto.senderId() + "님이 채팅방에 참여하였습니다.");
        } else {
            // 이미 존재하는 채팅방에 입장 메시지를 전송
            requestDto = new ChatMessageRequestDto(existingRoom.getId(), requestDto.senderId(), requestDto.mogakkoId(), requestDto.senderId() + "님이 채팅방에 참여하였습니다.");
        }

        template.convertAndSend("/sub/chat/room/" + requestDto.chatRoomId(), requestDto);
    }

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto) {
        ChatMessageDto message = chatRoomService.saveChatMessage(requestDto);
        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }
}
