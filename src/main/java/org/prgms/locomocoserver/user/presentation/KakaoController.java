package org.prgms.locomocoserver.user.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.kakao.KakaoTokenResponseDto;
import org.prgms.locomocoserver.user.dto.kakao.KakaoUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class KakaoController {

    private final UserService userService;

    @Value("${oauth.kakao.REST_API_KEY}")
    private String kakao_api_key;
    @Value("${oauth.kakao.REDIRECT_URI}")
    private String kakao_redirect_uri;

    @GetMapping("/users/login/kakao")
    public void getKakaoLoginAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://kauth.kakao.com/oauth/authorize?client_id="
                + kakao_api_key + "&redirect_uri=" + kakao_redirect_uri + "&response_type=code");
    }

    @GetMapping("/users/login/kakao/callback")
    public ResponseEntity<KakaoTokenResponseDto> getKakaoLoginCallback(@RequestParam(name = "code") String code) throws JsonProcessingException {
        KakaoTokenResponseDto tokenResponseDto = getTokenDto(code);
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = loadUserInfo(tokenResponseDto.accessToken());
        userService.saveOrUpdate(kakaoUserInfoResponseDto);

        return ResponseEntity.ok(tokenResponseDto);
    }

    @GetMapping("/users/kakao/me")
    @ResponseBody
    public ResponseEntity<KakaoUserInfoResponseDto> getUserInfo(@RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        KakaoUserInfoResponseDto responseDto = loadUserInfo(accessToken);
        return ResponseEntity.ok(responseDto);
    }

    private KakaoTokenResponseDto getTokenDto(String code) throws JsonProcessingException {
        // Kakao 인증 서버 엔드포인트 URL
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP 요청 바디 설정
        String requestBody = "grant_type=authorization_code" +
                "&client_id=" + kakao_api_key +
                "&redirect_uri=" + kakao_redirect_uri +
                "&code=" + code;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), String.class);

        // 응답 본문에서 DTO로 수동 변환
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), KakaoTokenResponseDto.class);
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
