package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.application.WebSocketChatHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatController {

    private final WebSocketChatHandler handler;
}
