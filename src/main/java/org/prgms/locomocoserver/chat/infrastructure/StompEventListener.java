package org.prgms.locomocoserver.chat.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatActivityService;
import org.prgms.locomocoserver.chat.application.ChatMessagePolicy;
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

import static org.prgms.locomocoserver.chat.infrastructure.StompChatService.DESTINATION_ROUTE;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompEventListener {

    private final ChatActivityService chatActivityService;
    private final ChatMessagePolicy chatMessagePolicy;
    private final SessionRegistry sessionRegistry;

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        User user = sessionRegistry.getUser(sessionId);

        if (user == null) {
            log.error("Client connected: {} (User: Unknown)", sessionId);
            throw new ChatException(ChatErrorType.CHAT_PARTICIPANT_NOT_FOUND, "Client connected: " + sessionId);
        }

        log.info("Client connected: {} (User: {})", sessionId, user.getNickname());
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        User user = sessionRegistry.getUser(sessionId);

        if (destination == null) {
            removeSession(sessionId);
            log.info("Client disconnected : {} (User: {})", sessionId, user.getNickname());
            return;
        }

        handleUserDisconnection(sessionId, user, extractChatRoomId(destination));
        removeSession(sessionId);
    }

    private Long extractChatRoomId(String destination) {
        return Long.parseLong(destination.substring(DESTINATION_ROUTE.length()));
    }

    private void handleUserDisconnection(String sessionId, User user, Long chatRoomId) {
        if (user == null) {
            log.error("Client disconnected: {} (User: Unknown)", sessionId);
            return;
        }

        log.info("Client disconnected: {} (User: {})", sessionId, user.getNickname());

        ChatMessageDto lastChatMessage = chatMessagePolicy.getLastChatMessage(chatRoomId);
        chatActivityService.updateLastReadMessage(chatRoomId, new ChatActivityRequestDto(user.getId(), lastChatMessage.chatMessageId()));
    }

    private void removeSession(String sessionId) {
        sessionRegistry.removeSession(sessionId);
    }
}
