package org.prgms.locomocoserver.user.presentation;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.response.UserInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PutMapping("/users/init/{userId}")
    public ResponseEntity<UserInfoDto> getInitInfo(@PathVariable Long userId,
                                                   @RequestBody UserInitInfoRequestDto requestDto) {
        UserInfoDto userInfoDto = userService.getInitInfo(userId, requestDto);
        return ResponseEntity.ok(userInfoDto);
    }

    @GetMapping("/users/nickname/{nickname}/check")
    public ResponseEntity<Void> checkNicknameAvailability(@PathVariable String nickname) {
        boolean isUnique = userService.isNicknameUnique(nickname);
        if (isUnique) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
