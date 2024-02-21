package org.prgms.locomocoserver.chat.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.dto.ChatMessageDto;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ChatRoom Controller", description = "채팅방 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 목록 조회", description = "userId 기반 채팅방 조회")
    @GetMapping("/chats/rooms/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms(@PathVariable Long userId,
                                                             @RequestParam(name = "cursor", required = false) String cursor,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        List<ChatRoomDto> chatRoomDtos = chatRoomService.getAllChatRoom(userId, cursor, pageSize);
        return ResponseEntity.ok(chatRoomDtos);
    }

    @GetMapping("/chats/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getAllChatMessages(@PathVariable Long roomId,
                                                                   @RequestParam(name = "cursor", required = false) String cursor,
                                                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        List<ChatMessageDto> chatMessageDtos = chatRoomService.getAllChatMessages(roomId, cursor, pageSize);
        return ResponseEntity.ok(chatMessageDtos);
    }

}
