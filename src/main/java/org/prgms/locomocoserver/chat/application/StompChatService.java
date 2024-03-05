package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StompChatService {

    private final SimpMessagingTemplate template;

    public void sendToSubscribers(ChatMessageDto chatMessageDto) {
        template.convertAndSend("/sub/chat/room/" + chatMessageDto.chatRoomId(), chatMessageDto);
    }
}
