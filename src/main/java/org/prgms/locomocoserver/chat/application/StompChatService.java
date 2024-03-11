package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StompChatService {

    private final SimpMessagingTemplate template;

    public void sendToSubscribers(ChatMessageDto chatMessageDto) {
        log.info("sendToSubscribers: " + chatMessageDto.message() + " " + chatMessageDto.senderNickName());
        template.convertAndSend("/sub/chat/room/" + chatMessageDto.chatRoomId(), chatMessageDto);
    }
}
