package org.prgms.locomocoserver.chat.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.user.application.TokenService;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final TokenService tokenService;
    private final SessionRegistry sessionRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            String provider = accessor.getFirstNativeHeader("provider");

            if (authorization == null || !tokenService.isValidToken(authorization, provider)) {
                throw new AuthException(ErrorCode.UNAUTHORIZED, "Invalid authorization");
            }

            try {
                User user = tokenService.getUserFromToken(authorization.substring(7), provider);

                String sessionId = accessor.getSessionId();
                sessionRegistry.addSession(sessionId, user);
            } catch (JsonProcessingException e) {
                throw new UserException(UserErrorType.USER_NOT_FOUND);
            }
        }

        return message;
    }

}
