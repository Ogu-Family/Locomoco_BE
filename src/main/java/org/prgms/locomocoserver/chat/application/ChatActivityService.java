package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.domain.ChatParticipant;
import org.prgms.locomocoserver.chat.domain.mongo.ChatMessageMongoCustomRepository;
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
    private final ChatMessageMongoCustomRepository chatMessageMongoCustomRepository;

    @Transactional
    public void updateLastReadMessage(Long chatRoomId, ChatActivityRequestDto requestDto) {
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserIdAndChatRoomId(requestDto.userId(), chatRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorType.CHAT_PARTICIPANT_NOT_FOUND));
        chatParticipant.updateLastReadMessageId(requestDto.lastReadMessageId());
    }

    @Transactional(readOnly = true)
    public int unReadMessageCount(Long roomId, String lastReadMsgId) {
        if (lastReadMsgId == null) {
            return 0;
        }
        return chatMessageMongoCustomRepository.unReadMessageCount(roomId, lastReadMsgId);
    }
}
