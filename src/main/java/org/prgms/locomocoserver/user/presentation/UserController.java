package org.prgms.locomocoserver.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoInfoDto;
import org.prgms.locomocoserver.mogakkos.dto.response.MogakkoSimpleInfoResponseDto;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.request.UserInitInfoRequestDto;
import org.prgms.locomocoserver.user.dto.request.UserUpdateRequest;
import org.prgms.locomocoserver.user.dto.response.UserInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
                                                   @RequestPart("requestDto") UserInitInfoRequestDto requestDto,
                                                   @RequestPart(value = "file", required = false) MultipartFile multipartFile) {
        UserInfoDto userInfoDto = userService.insertInitInfo(userId, requestDto, multipartFile);
        return ResponseEntity.ok(userInfoDto);
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 id로 탈퇴를 진행합니다")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserInfoDto> deleteUser(@PathVariable Long userId) {
        UserInfoDto userInfoDto = userService.deleteUser(userId);
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

    @Operation(summary = "찜한 모각코 목록 조회", description = "사용자가 좋아요를 누른 모각코 목록을 조회합니다.")
    @GetMapping("/users/{userId}/mogakko/like")
    public ResponseEntity<List<MogakkoSimpleInfoResponseDto>> getLikedMogakkos(@PathVariable Long userId) {
        List<MogakkoSimpleInfoResponseDto> mogakkoInfoDtos = userService.getLikedMogakkos(userId);
        return ResponseEntity.ok(mogakkoInfoDtos);
    }

    @Operation(summary = "사용자 정보 수정", description = "프로필 이미지, 닉네임, 성별, 생년월일, 직업을 수정할 수 있습니다.")
    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserInfoDto> updateUserInfo(@PathVariable Long userId,
                                                      @RequestPart("requestDto") UserUpdateRequest requestDto,
                                                      @RequestPart(value = "file", required = false) MultipartFile multipartFile) throws IOException {
        UserInfoDto userInfoDto = userService.updateUserInfo(userId, requestDto, multipartFile);
        return ResponseEntity.ok(userInfoDto);
    }

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드 합니다.")
    @PostMapping("/users/{userId}/profile-image")
    public ResponseEntity<UserInfoDto> uploadProfileImage(@PathVariable Long userId, @RequestPart("file") MultipartFile multipartFile) throws IOException {
        UserInfoDto userInfoDto = userService.uploadProfileImage(userId, multipartFile);
        return ResponseEntity.ok(userInfoDto);
    }

    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제 합니다.")
    @DeleteMapping("/users/{userId}/profile-image")
    public ResponseEntity<UserInfoDto> deleteProfileImage(@PathVariable Long userId) {
        UserInfoDto userInfoDto = userService.deleteProfileImage(userId);
        return ResponseEntity.ok(userInfoDto);
    }
    
}
