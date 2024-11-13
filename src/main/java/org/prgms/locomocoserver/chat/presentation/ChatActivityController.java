package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.ChatActivityService;
import org.prgms.locomocoserver.chat.dto.request.ChatActivityRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatActivityController {

    private final ChatActivityService chatActivityService;

    @PatchMapping("/chats/activity/{chatRoomId}/last-read-message")
    public ResponseEntity<Void> updateLastReadMessage(@PathVariable Long chatRoomId,
                                                      @RequestBody ChatActivityRequestDto request) {
        chatActivityService.updateLastReadMessage(chatRoomId, request);
        return ResponseEntity.ok().build();
    }
}

