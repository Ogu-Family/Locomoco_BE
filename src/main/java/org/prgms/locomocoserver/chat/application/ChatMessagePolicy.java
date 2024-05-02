package org.prgms.locomocoserver.chat.application;

import org.prgms.locomocoserver.chat.domain.ChatRoom;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.user.domain.User;

import java.util.List;

public interface ChatMessagePolicy {
    ChatMessageDto saveEnterMessage(Long roomId, User sender);
    ChatMessageDto saveChatMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto);
    List<ChatMessageDto> getAllChatMessages(Long roomId, String cursor, int pageSize);
    void deleteChatMessages(Long roomId);
}
