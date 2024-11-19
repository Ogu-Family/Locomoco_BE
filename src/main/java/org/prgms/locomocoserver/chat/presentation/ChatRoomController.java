package org.prgms.locomocoserver.chat.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatActivityService;
import org.prgms.locomocoserver.chat.application.ChatMessagePolicy;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.application.MySqlChatMessageService;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.prgms.locomocoserver.chat.dto.request.ChatActivityRequestDto;
import org.prgms.locomocoserver.global.annotation.GetUser;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ChatRoom Controller", description = "채팅방 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatActivityService chatActivityService;

    @Operation(summary = "채팅방 목록 조회", description = "userId 기반 채팅방 조회")
    @GetMapping("/chats/rooms/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms(@PathVariable Long userId,
                                                             @RequestParam(name = "cursor", required = false) String cursor,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        List<ChatRoomDto> chatRoomDtos = chatRoomService.getAllChatRoom(userId, cursor, pageSize);
        return ResponseEntity.ok(chatRoomDtos);
    }

    @Operation(summary = "채팅방 메시지 내용 조회", description = "roomId 기반 채팅방 조회")
    @GetMapping("/chats/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getAllChatMessages(@PathVariable Long roomId,
                                                                   @RequestParam(name = "cursor", required = false) String cursor,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                                   @GetUser User user) {
        List<ChatMessageDto> chatMessageDtos = chatRoomService.getAllChatMessages(roomId, cursor, pageSize);
        chatActivityService.updateLastReadMessage(roomId, new ChatActivityRequestDto(user.getId(), chatMessageDtos.get(chatMessageDtos.size()-1).chatMessageId()));

        return ResponseEntity.ok(chatMessageDtos);
    }

    @Operation(summary = "모각코 id로 채팅방 id 조회", description = "모각코와 연결되어 있는 채팅방 id를 반환합니다.")
    @GetMapping("/chats/room/mogakko/{mogakkoId}")
    public ResponseEntity<Long> getChatRoomIdByMogakkoId(@PathVariable Long mogakkoId) {
        Long chatRoomId = chatRoomService.getChatRoomIdByMogakkoId(mogakkoId);
        return ResponseEntity.ok(chatRoomId);
    }

}
