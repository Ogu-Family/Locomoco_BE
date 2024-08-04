package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatMessage;
import org.prgms.locomocoserver.chat.domain.ChatMessageRepository;
import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.domain.ChatRoomRepository;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MySqlChatMessageService implements ChatMessagePolicy {

    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public ChatMessageDto saveEnterMessage(Long roomId, User sender) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));

        ChatMessage chatMessage = chatMessageRepository.save(toEnterMessage(chatRoom, sender));
        chatRoom.updateUpdatedAt();

        return ChatMessageDto.of(chatMessage);
    }

    @Override
    @Transactional
    public ChatMessageDto saveChatMessage(Long roomId, ChatMessageRequestDto requestDto) {
        User sender = userService.getById(requestDto.senderId());
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedAtIsNull(requestDto.chatRoomId())
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));

        ChatMessage chatMessage = chatMessageRepository.save(requestDto.toChatMessageEntity(sender, chatRoom, false));
        chatRoom.updateUpdatedAt();

        return ChatMessageDto.of(chatMessage);
    }

    @Override
    public ChatMessageDto saveChatMessageWithImage(Long roomId, List<String> imageUrls, ChatMessageRequestDto chatMessageRequestDto) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, String cursorValue, int pageSize) {
        Long cursor = Long.MAX_VALUE;
        if (cursorValue != "null") cursor = Long.parseLong(cursorValue);

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomIdAndIdGreaterThan(roomId, cursor, pageSize);

        List<ChatMessageDto> chatMessageDtos = chatMessages.stream()
                .map(ChatMessageDto::of)
                .collect(Collectors.toList());
        Collections.reverse(chatMessageDtos);

        return chatMessageDtos;
    }

    @Transactional
    public void deleteChatMessages(ChatRoom chatRoom) {
        chatMessageRepository.findAllByChatRoom(chatRoom).forEach(chatMessage -> chatMessage.delete());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatMessageDto getLastChatMessage(Long roomId) {
        return ChatMessageDto.of(chatMessageRepository.findLastMessageByRoomId(roomId));
    }

    private ChatMessage toEnterMessage(ChatRoom chatRoom, User participant) {
        return ChatMessage.builder().chatRoom(chatRoom).sender(
                participant).content(participant.getNickname() + "님이 입장하셨습니다.").isNotice(true).build();
    }
}
