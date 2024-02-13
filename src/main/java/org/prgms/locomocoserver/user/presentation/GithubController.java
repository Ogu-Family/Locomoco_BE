package org.prgms.locomocoserver.user.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.github.GithubTokenResponseDto;
import org.prgms.locomocoserver.user.dto.github.GithubUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class GithubController {

    private final UserService userService;

    @Value("${oauth.github.CLIENT_ID}")
    private String github_client_id;

    @Value("${oauth.github.CLIENT_SECRET_KEY}")
    private String github_client_secret_key;

    @Value("${oauth.github.REDIRECT_URI}")
    private String github_redirect_url;

    @GetMapping("/users/login/github")
    public void getGithubAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://github.com/login/oauth/authorize?client_id=" + github_client_id);
    }

    @GetMapping("/users/login/github/callback")
    public ResponseEntity<GithubTokenResponseDto> getGithubLoginCallback(@RequestParam(name = "code") String code) {
        GithubTokenResponseDto githubTokenResponseDto = getTokenDto(code);

        return ResponseEntity.ok(githubTokenResponseDto);
    }

    @GetMapping("/users/github/me")
    public ResponseEntity<GithubUserInfoResponseDto> getUserInfo(@RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        GithubUserInfoResponseDto userInfo = loadUserInfo(accessToken);
        return ResponseEntity.ok(userInfo);
    }

    private GithubTokenResponseDto getTokenDto(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GithubTokenResponseDto> response = restTemplate.postForEntity(
                tokenUrl + "?client_id={clientId}&client_secret={clientSecret}&code={code}&redirect_uri={redirectUri}",
                null, GithubTokenResponseDto.class, github_client_id, github_client_secret_key, code, github_redirect_url);
        return response.getBody();
    }

    private GithubUserInfoResponseDto loadUserInfo(String accessToken) throws JsonProcessingException {
        String apiUrl = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        // 응답으로 받은 JSON 데이터를 배열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        GithubUserInfoResponseDto[] userInfos = objectMapper.readValue(response.getBody(), GithubUserInfoResponseDto[].class);

        // primary 값이 true인 이메일 찾기
        for (GithubUserInfoResponseDto userInfo : userInfos) {
            if (userInfo.primary()) {
                return userInfo;
            }
        }

        // primary email이 없는 경우
        return null;
    }

}
