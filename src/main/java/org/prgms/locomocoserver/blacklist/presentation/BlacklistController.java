package org.prgms.locomocoserver.blacklist.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.blacklist.application.BlacklistService;
import org.prgms.locomocoserver.blacklist.dto.request.BlacklistRequestDto;
import org.prgms.locomocoserver.global.common.dto.Results;
import org.prgms.locomocoserver.user.dto.response.UserBriefInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blacklist Controller", description = "블랙 리스트 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BlacklistController {

    private final BlacklistService blacklistService;

    @Operation(summary = "특정 유저의 블랙리스트 조회", description = "특정 유저가 등록한 블랙리스트를 20개씩 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적인 블랙리스트 반환. 최대 20개")
    @GetMapping("/users/{userId}/blacklist")
    public ResponseEntity<Results<UserBriefInfoDto>> findAll(
        @Parameter(description = "유저 id") @PathVariable Long userId,
        @Parameter(description = "DB 인덱스 커서") @RequestParam(required = false, defaultValue = "0") Long cursor) {
        Results<UserBriefInfoDto> responseDto = blacklistService.findAll(cursor, userId);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "블랙리스트 등록", description = "특정 유저가 다른 유저를 블랙리스트로 등록합니다.")
    @ApiResponse(responseCode = "200", description = "블랙리스트 등록 성공")
    @PostMapping("/users/{userId}/blacklist")
    public ResponseEntity<Void> black(
        @Parameter(description = "유저 id") @PathVariable Long userId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "블랙리스트로 등록할 유저 id를 넣어줍니다") @RequestBody BlacklistRequestDto requestDto) {
        blacklistService.black(userId, requestDto);

        return ResponseEntity.ok().build();
    }
}
