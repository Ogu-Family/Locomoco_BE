package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;

    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chats/enter")
    public void enter(ChatMessageDto message) {
        message = new ChatMessageDto(message.chatRoomId(), message.senderId(), message.senderId() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageDto message) {
        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }
}
