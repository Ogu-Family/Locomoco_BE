package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.querydsl.ChatParticipantCustomRepository;
import org.prgms.locomocoserver.chat.dto.request.ChatActivityRequestDto;
import org.prgms.locomocoserver.chat.exception.ChatErrorType;
import org.prgms.locomocoserver.chat.exception.ChatException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatActivityService {

    private final ChatParticipantCustomRepository chatParticipantRepository;

    @Transactional
    public void updateLastReadMessage(Long chatRoomId, ChatActivityRequestDto requestDto) {
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserIdAndChatRoomId(requestDto.userId(), chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHAT_PARTICIPANT_NOT_FOUND));
        chatParticipant.updateLastReadMessageId(requestDto.lastReadMessageId());
    }
}
