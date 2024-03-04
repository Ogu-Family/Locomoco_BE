package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @MessageMapping(value = "/chats/enter")
    public void enter(ChatMessageRequestDto requestDto) {

        ChatRoomDto chatRoomDto = chatRoomService.enterChatRoom(requestDto);
        User sender = userService.getById(requestDto.senderId());
        requestDto = new ChatMessageRequestDto(chatRoomDto.roomId(), requestDto.senderId(), requestDto.mogakkoId(), sender.getNickname() + "님이 채팅방에 참여하였습니다.");

        template.convertAndSend("/sub/chat/room/" + requestDto.chatRoomId(), requestDto);
    }

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto) {
        log.info("Request Message : " + requestDto.senderId() + " " + requestDto.message());
        ChatMessageDto message = chatRoomService.saveChatMessage(requestDto);
        log.info("After Message : " + message.chatRoomId() + " " + message.message() + " " + message.senderNickName());

        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }

}
