package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatActivityRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.prgms.locomocoserver.chat.application.StompChatService.DESTINATION_ROUTE;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private final ChatActivityService chatActivityService;
    private final ChatMessagePolicy chatMessagePolicy;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // 세션에서 user 속성 가져오기
        User user = (User) headerAccessor.getSessionAttributes().get("user");
        String sessionId = headerAccessor.getSessionId();

        if (user != null) {
            log.info("Client connected: " + sessionId + " (User: " + user.getNickname() + ")");
        } else {
            log.error("Client connected: " + sessionId + " (User: Unknown)");
            throw new ChatException(ChatErrorType.CHAT_PARTICIPANT_NOT_FOUND, "Client connected: " + sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // 세션에서 user 속성 가져오기
        User user = (User) headerAccessor.getSessionAttributes().get("user");
        String sessionId = headerAccessor.getSessionId();
        Long chatRoomId = Long.parseLong(headerAccessor.getDestination().substring(DESTINATION_ROUTE.length()));

        if (user!= null) {
            log.info("Client disconnected: " + sessionId + " (User: " + user.getNickname() + ")");

            ChatMessageDto chatMessageDto = chatMessagePolicy.getLastChatMessage(chatRoomId);
            chatActivityService.updateLastReadMessage(chatRoomId, new ChatActivityRequestDto(user.getId(), chatMessageDto.chatMessageId()));
        } else {
            log.error("Client disconnected: " + sessionId + " (User: Unknown)");
        }
    }
}
