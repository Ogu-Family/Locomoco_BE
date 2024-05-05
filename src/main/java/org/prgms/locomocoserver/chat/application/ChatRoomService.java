package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.*;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatCreateRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatEnterRequestDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    private final StompChatService stompChatService;
    private final MySqlChatMessageService mySqlChatMessageService;
    private final MongoChatMessageService mongoChatMessageService;

    private final ChatMessagePolicy chatMessagePolicy; // TODO : service 두가지 의존 삭제

    @Transactional
    public void enterChatRoom(ChatEnterRequestDto requestDto) {
        ChatRoom chatRoom = getById(requestDto.chatRoomId());

        if (!isParticipantExist(chatRoom, requestDto.participant())) {
            ChatMessageDto chatMessageDto = saveEnterMessage(requestDto);  // TODO : mysql chat
            mongoChatMessageService.saveEnterMessage(requestDto.chatRoomId(), requestDto.participant()); // TODO : mongo chat
            ChatParticipant chatParticipant = chatParticipantRepository.save(ChatParticipant.builder().user(requestDto.participant())
                    .chatRoom(chatRoom).build());

            chatRoom.addChatParticipant(chatParticipant);
            stompChatService.sendToSubscribers(chatMessageDto);
        }
    }

    @Transactional
    public ChatRoom createChatRoom(ChatCreateRequestDto requestDto) {
        ChatRoom chatRoom = requestDto.toChatRoomEntity();
        ChatParticipant chatParticipant = chatParticipantRepository.save(ChatParticipant.builder().user(requestDto.creator())
                .chatRoom(chatRoom).build());

        chatRoom.addChatParticipant(chatParticipant);
        chatRoomRepository.save(chatRoom);
        mySqlChatMessageService.saveEnterMessage(chatRoom.getId(), chatParticipant.getUser());  // TODO : mysql chat
        mongoChatMessageService.saveEnterMessage(chatRoom.getId(), requestDto.creator()); // TODO : mongo chat

        return chatRoom;
    }

    @Transactional
    public ChatMessageDto saveChatMessage(ChatMessageRequestDto requestDto) {
        return mySqlChatMessageService.saveChatMessage(requestDto.chatRoomId(), requestDto);  // TODO : policy 변경
    }

    @Transactional
    public ChatMessageDto saveEnterMessage(ChatEnterRequestDto requestDto) {
        return mySqlChatMessageService.saveEnterMessage(requestDto.chatRoomId(), requestDto.participant()); // TODO : policy 변경
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto> getAllChatRoom(Long userId, Long cursor, int pageSize) {
        if (cursor == null) cursor = Long.MAX_VALUE;
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipantsId(userId, cursor, pageSize);

        List<ChatRoomDto> chatRoomDtos = chatRooms.stream()
                .map(chatRoom -> ChatRoomDto.of(chatRoom, mySqlChatMessageService.getLastChatMessage(chatRoom.getId()))) // TODO : policy 교체
                .toList();
        return chatRoomDtos;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessages(Long roomId, Long cursor, int pageSize) {
        return mongoChatMessageService.getAllChatMessages(roomId, String.valueOf(cursor), pageSize); // TODO : mongo 페이지네이션 후 policy 교체
    }

    @Transactional(readOnly = true)
    public ChatRoom getById(Long id) {
        return chatRoomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Long getChatRoomIdByMogakkoId(Long mogakkoId) {
        ChatRoom chatRoom = chatRoomRepository.findByMogakkoId(mogakkoId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHATROOM_NOT_FOUND, "해당 모각코의 채팅방이 존재하지 않습니다. : mogakkoId[" + mogakkoId + "]"));
        return chatRoom.getId();
    }

    @Transactional
    public void leave(ChatRoom chatRoom, Long userId) {
        chatParticipantRepository.deleteByChatRoomIdAndUserId(chatRoom.getId(), userId);
    }

    @Transactional
    public void delete(ChatRoom chatRoom) {
        // 채팅방 참여자 목록 삭제
        chatParticipantRepository.deleteAllByChatRoom(chatRoom);
        // 채팅방 메시지 삭제
        mySqlChatMessageService.deleteChatMessages(chatRoom); // TODO : mysql
        mongoChatMessageService.deleteChatMessages(chatRoom); // TODO : mongo
        // 채팅방 삭제
        chatRoom.delete();
    }

    private boolean isParticipantExist(ChatRoom chatRoom, User user) {
        return chatRoom.getChatParticipants().stream()
                .anyMatch(chatParticipant -> chatParticipant.getUser().getId().equals(user.getId()));
    }
}
