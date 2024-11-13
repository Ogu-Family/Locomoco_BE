package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.prgms.locomocoserver.chat.domain.mongo.ChatActivity;
import org.prgms.locomocoserver.chat.domain.mongo.ChatActivityRepository;
import org.prgms.locomocoserver.chat.dto.request.ChatActivityRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatActivityService {

    private final ChatActivityRepository chatActivityRepository;

    @Transactional
    public void updateLastReadMessage(Long chatRoomId, ChatActivityRequestDto requestDto) {
        ChatActivity chatActivity = chatActivityRepository.findByUserIdAndChatRoomId(requestDto.userId().toString(), String.valueOf(chatRoomId))
                .orElseThrow(() -> new ChatException(ChatErrorType.CHAT_PARTICIPANT_NOT_FOUND));
        chatActivity.updateLastReadMessage(requestDto.userId().toString(), new ObjectId(requestDto.lastReadMessageId()));
    }

}
