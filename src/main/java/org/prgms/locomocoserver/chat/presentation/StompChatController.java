package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatImageService;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.infrastructure.StompChatService;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.springframework.messaging.handler.annotation.Header;
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
    private final AuthenticationService authenticationService;

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto, @Header("Authorization") String accessToken, @Header("provider") String provider) {
        ChatMessageDto message; List<String> imageUrls = null;

        boolean isValidToken = authenticationService.authenticateUser(provider, accessToken);
        if(!isValidToken) {
            throw new AuthException(ErrorCode.UNAUTHORIZED);
        }

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
