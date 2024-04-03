package org.prgms.locomocoserver.user.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.user.application.RefreshTokenService;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.kakao.KakaoUserInfoResponseDto;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Tag(name = "Kakao Login Controller", description = "카카오 로그인 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class KakaoController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Value("${oauth.kakao.REST_API_KEY}")
    private String kakao_api_key;
    @Value("${oauth.kakao.REDIRECT_URI}")
    private String kakao_redirect_uri;
    @Value("${oauth.kakao.REDIRECT_URI_LOCAL}")
    private String kakao_redirect_uri_local;

    @Operation(summary = "카카오 로그인 페이지", description = "카카오 로그인 페이지 반환")
    @GetMapping("/users/login/kakao")
    public void getKakaoLoginAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://kauth.kakao.com/oauth/authorize?client_id="
                + kakao_api_key + "&redirect_uri=" + kakao_redirect_uri + "&response_type=code");
    }

    @Operation(summary = "로그인 후 리다이렉트 uri", description = "자동 redirect 되어 token 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @Parameter(name = "code", description = "로그인 정보 입력 시 카카오에서 반환되는 일회성 code")
    @GetMapping("/users/login/kakao/callback")
    public ResponseEntity<UserLoginResponse> getKakaoLoginCallback(@RequestParam(name = "code") String code,
                                                                   HttpServletRequest request) throws JsonProcessingException {
        log.info("KakaoController.getKakaoLoginCallback " + code);

        String origin = request.getHeader("Origin");
        TokenResponseDto tokenResponseDto = origin.contains("local") ? getTokenDto(code, kakao_redirect_uri_local) : getTokenDto(code, kakao_redirect_uri);
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = loadUserInfo(tokenResponseDto.accessToken());

        log.info("KakaoController.getKakaoLoginCallback after loadUserInfo : " + kakaoUserInfoResponseDto.getEmail());

        UserLoginResponse userLoginResponse = userService.saveOrUpdate(kakaoUserInfoResponseDto, tokenResponseDto);

        log.info("KakaoController.getKakaoLoginCallback after saveOrUpdate : " + userLoginResponse.isNewUser());

        return ResponseEntity.ok(userLoginResponse);
    }

    @Operation(summary = "accessToken 재발급", description = "accessToken이 만료되어 재발급합니다.")
    @GetMapping("/users/refresh/kakao")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestHeader String accessToken) {
        TokenResponseDto tokenResponseDto = refreshTokenService.updateAccessToken(accessToken);
        return ResponseEntity.ok(tokenResponseDto);
    }

    @Operation(summary = "로그인된 사용자 정보 조회", description = "access token으로 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 정보 조회 성공")
    @GetMapping("/users/kakao/me")
    @ResponseBody
    public ResponseEntity<KakaoUserInfoResponseDto> getUserInfo(@RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        KakaoUserInfoResponseDto responseDto = loadUserInfo(accessToken);
        return ResponseEntity.ok(responseDto);
    }

    private TokenResponseDto getTokenDto(String code, String redirectUri) throws JsonProcessingException {
        // Kakao 인증 서버 엔드포인트 URL
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP 요청 바디 설정
        String requestBody = "grant_type=authorization_code" +
                "&client_id=" + kakao_api_key +
                "&redirect_uri=" + redirectUri +
                "&code=" + code;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);

        // 응답 본문에서 DTO로 수동 변환
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), TokenResponseDto.class);
    }

    private KakaoUserInfoResponseDto loadUserInfo(String accessToken) throws JsonProcessingException {
        // Kakao API 엔드포인트 URL
        String apiUrl = "https://kapi.kakao.com/v2/user/me";

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // REST 템플릿을 사용하여 GET 요청 전송
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), KakaoUserInfoResponseDto.class);
    }
}
