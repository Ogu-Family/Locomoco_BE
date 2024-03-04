package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template;
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping(value = "/chats/enter")
    public void enter(ChatMessageRequestDto requestDto) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(requestDto.chatRoomId());

        ChatMessageDto chatMessageDto;
        if (chatRoom != null && !chatRoomService.isParticipantExist(chatRoom.get(), requestDto.senderId())) {
            chatMessageDto = chatRoomService.saveEnterMessage(requestDto);
            chatRoomService.addParticipant(chatRoom.get(), requestDto.senderId());
            template.convertAndSend("/sub/chat/room/" + requestDto.chatRoomId(), chatMessageDto);
        }

        chatRoomService.enterChatRoom(requestDto);
    }

    @MessageMapping(value = "/chats/message")
    public void message(ChatMessageRequestDto requestDto) {
        log.info("Request Message : " + requestDto.senderId() + " " + requestDto.message());
        ChatMessageDto message = chatRoomService.saveChatMessage(requestDto);
        log.info("After Message : " + message.chatRoomId() + " " + message.message() + " " + message.senderNickName());

        template.convertAndSend("/sub/chat/room/" + message.chatRoomId(), message);
    }

}
