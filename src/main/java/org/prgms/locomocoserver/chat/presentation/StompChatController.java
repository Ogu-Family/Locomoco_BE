package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatImageService;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.application.StompChatService;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final ChatRoomService chatRoomService;
    private final ChatImageService chatImageService;
    private final StompChatService stompChatService;

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto) {
        ChatMessageDto message;
        List<String> imageUrls = null;
        if (requestDto.imageByteCode() != null) {
            imageUrls = chatImageService.create(requestDto);
            message = chatRoomService.saveChatMessageWithImage(requestDto.chatRoomId(), imageUrls, requestDto);
        }else {
            message = chatRoomService.saveChatMessage(requestDto);
        }
        log.info("After Message : " + message.chatRoomId() + " " + message.message() + " " + message.senderNickName());
        stompChatService.sendToSubscribers(message);
    }

}
