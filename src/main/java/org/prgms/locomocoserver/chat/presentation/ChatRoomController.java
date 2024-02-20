package org.prgms.locomocoserver.chat.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.dto.ChatRoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "ChatRoom Controller", description = "채팅방 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chats/rooms/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms(@PathVariable Long userId) {
        List<ChatRoomDto> chatRoomDtos = chatRoomService.getAllChatRoom(userId);
        return ResponseEntity.ok(chatRoomDtos);
    }
}
