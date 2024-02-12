package org.prgms.locomocoserver.user.presentation;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.UserService;
import org.prgms.locomocoserver.user.dto.github.GithubTokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    private GithubTokenResponseDto getTokenDto(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GithubTokenResponseDto> response = restTemplate.postForEntity(
                tokenUrl + "?client_id={clientId}&client_secret={clientSecret}&code={code}&redirect_uri={redirectUri}",
                null, GithubTokenResponseDto.class, github_client_id, github_client_secret_key, code, github_redirect_url);
        return response.getBody();
    }

}
