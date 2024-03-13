package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.application.StompChatService;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final ChatRoomService chatRoomService;
    private final StompChatService stompChatService;

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto) {
        log.info("Request Message : " + requestDto.senderId() + " " + requestDto.message());
        ChatMessageDto message = chatRoomService.saveChatMessage(requestDto);
        log.info("After Message : " + message.chatRoomId() + " " + message.message() + " " + message.senderNickName());

        stompChatService.sendToSubscribers(message);
    }

}
