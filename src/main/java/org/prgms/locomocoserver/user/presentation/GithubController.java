package org.prgms.locomocoserver.user.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.github.GithubUserInfoResponseDto;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.dto.response.UserLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Tag(name = "Github Login Controller", description = "깃허브 로그인 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class GithubController {

    private final UserService userService;

    @Value("${oauth.github.CLIENT_ID}")
    private String github_client_id;

    @Value("${oauth.github.CLIENT_SECRET_KEY}")
    private String github_client_secret_key;

    @Value("${oauth.github.REDIRECT_URI}")
    private String github_redirect_url;

    @Operation(summary = "깃허브 로그인 페이지", description = "깃허브 로그인 페이지 반환")
    @GetMapping("/users/login/github")
    public void getGithubAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://github.com/login/oauth/authorize?client_id=" + github_client_id);
    }

    @Operation(summary = "로그인 후 리다이렉트 uri", description = "자동 redirect 되어 token 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @Parameter(name = "code", description = "로그인 정보 입력 시 깃허브에서 반환되는 일회성 code")
    @GetMapping("/users/login/github/callback")
    public ResponseEntity<UserLoginResponse> getGithubLoginCallback(@RequestParam(name = "code") String code) throws JsonProcessingException {
        TokenResponseDto tokenResponseDto = getTokenDto(code);
        GithubUserInfoResponseDto githubUserInfoResponseDto = loadUserInfo(tokenResponseDto.accessToken());
        UserLoginResponse userLoginResponse = userService.saveOrUpdate(githubUserInfoResponseDto, tokenResponseDto);

        return ResponseEntity.ok(userLoginResponse);
    }

    @Operation(summary = "로그인된 사용자 정보 조회", description = "access token으로 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 정보 조회 성공")
    @GetMapping("/users/github/me")
    public ResponseEntity<GithubUserInfoResponseDto> getUserInfo(@RequestHeader("Authorization") String accessToken) throws JsonProcessingException {
        GithubUserInfoResponseDto userInfo = loadUserInfo(accessToken);
        return ResponseEntity.ok(userInfo);
    }

    private TokenResponseDto getTokenDto(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TokenResponseDto> response = restTemplate.postForEntity(
                tokenUrl + "?client_id={clientId}&client_secret={clientSecret}&code={code}&redirect_uri={redirectUri}",
                null, TokenResponseDto.class, github_client_id, github_client_secret_key, code, github_redirect_url);
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
        return userInfos[0];
    }

}
