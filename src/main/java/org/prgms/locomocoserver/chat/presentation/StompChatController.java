package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @MessageMapping(value = "/chats/enter")
    public void enter(ChatMessageRequestDto requestDto) {

        ChatRoomDto chatRoomDto = chatRoomService.enterChatRoom(requestDto.chatRoomId(), requestDto);
        User sender = userService.getById(requestDto.senderId());
        requestDto = new ChatMessageRequestDto(chatRoomDto.roomId(), requestDto.senderId(), requestDto.mogakkoId(), sender.getNickname() + "님이 채팅방에 참여하였습니다.");

        template.convertAndSend("/sub/chat/room/" + requestDto.chatRoomId(), requestDto);
    }

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto) {
        ChatMessageDto message = chatRoomService.saveChatMessage(requestDto);
        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }

    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessageDto chat(@DestinationVariable Long roomId, ChatMessageRequestDto message) {
        ChatRoomDto chatRoomDto = chatRoomService.enterChatRoom(roomId, message);

        return new ChatMessageDto(1l, message.chatRoomId(), message.senderId(), "nickname", null, message.message(), LocalDateTime.now());
    }
}
