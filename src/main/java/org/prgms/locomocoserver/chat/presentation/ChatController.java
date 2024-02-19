package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chats/enter")
    public void enter(ChatMessageDto message) {

        // 채팅방이 이미 존재하는지 확인합니다.
        ChatRoom existingRoom = chatRoomService.getById(message.chatRoomId());

        // 채팅방이 존재하지 않으면 새로운 채팅방을 생성
        if (existingRoom == null) {
            ChatRoomDto newRoom = chatRoomService.createChatRoom(message);
            message = new ChatMessageDto(newRoom.roomId(), message.mogakkoId(), message.senderId(), message.senderId() + "님이 채팅방에 참여하였습니다.");
        } else {
            // 이미 존재하는 채팅방에 입장 메시지를 전송
            message = new ChatMessageDto(existingRoom.getId(), message.mogakkoId(), message.senderId(), message.senderId() + "님이 채팅방에 참여하였습니다.");
        }

        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageDto message) {
        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }
}
