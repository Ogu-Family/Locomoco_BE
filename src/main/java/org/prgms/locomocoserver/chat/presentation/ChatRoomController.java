package org.prgms.locomocoserver.chat.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.application.ChatRoomService;
import org.prgms.locomocoserver.chat.dto.request.CreateChatRoomRequest;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.domain.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    //채팅방 개설
    @PostMapping(value = "/chats/room")
    public String create(@RequestBody CreateChatRoomRequest request, RedirectAttributes rttr) {
        log.info("# Create Chat Room , name: " + request.name());
        User loginUser = userService.getById(request.creatorId());
        rttr.addFlashAttribute("roomName", chatRoomService.createChatRoom(request, loginUser));
        return "redirect:/chat/rooms";
    }
}
