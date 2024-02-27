package org.prgms.locomocoserver.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.response.UserInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "User Controller", description = "사용자 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 초기 회원가입", description = "사용자 id로 초기 정보를 입력합니다.")
    @PutMapping("/users/init/{userId}")
    public ResponseEntity<UserInfoDto> getInitInfo(@PathVariable Long userId,
                                                   @RequestBody UserInitInfoRequestDto requestDto) {
        UserInfoDto userInfoDto = userService.getInitInfo(userId, requestDto);
        return ResponseEntity.ok(userInfoDto);
    }

    @Operation(summary = "회원가입 닉네임 중복확인", description = "닉네임 unique 여부를 확인합니다.")
    @GetMapping("/users/nickname/{nickname}/check")
    public ResponseEntity<Boolean> checkNicknameAvailability(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.isNicknameUnique(nickname));
    }

    @Operation(summary = "마이페이지 정보", description = "사용자 마이페이지 정보를 반환합니다.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable Long userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoDto);
    }

    @Operation(summary = "진행중인 모각코 목록 조회", description = "사용자가 진행중인 모각코 목록을 조회합니다.")
    @GetMapping("/users/{userId}/mogakko/ongoing")
    public ResponseEntity<List<MogakkoInfoDto>> getOngoingMogakkos(@PathVariable Long userId) {
        List<MogakkoInfoDto> mogakkoInfoDtos = userService.getOngoingMogakkos(userId);
        return ResponseEntity.ok(mogakkoInfoDtos);
    }

    @Operation(summary = "종료된 모각코 목록 조회", description = "사용자가 참여했던 모각코 목록을 조회합니다.")
    @GetMapping("/users/{userId}/mogakko/complete")
    public ResponseEntity<List<MogakkoInfoDto>> getCompleteMogakkos(@PathVariable Long userId) {
        List<MogakkoInfoDto> mogakkoInfoDtos = userService.getCompletedMogakkos(userId);
        return ResponseEntity.ok(mogakkoInfoDtos);
    }
    
}
