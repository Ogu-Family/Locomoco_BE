package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.global.exception.ExpiredTokenException;
import org.prgms.locomocoserver.global.exception.InvalidTokenException;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${oauth.github.CLIENT_ID}")
    private String github_client_id;

    @Value("${oauth.github.CLIENT_SECRET_KEY}")
    private String github_client_secret_key;

    public boolean authenticateUser(String providerValue, String accessToken) {
        Provider provider = Provider.valueOf(providerValue.toUpperCase());

        try {
            boolean isValidToken = false;
            switch (provider) {
                case KAKAO:
                    log.info("AuthenticationFilter.doFilter KAKAO authenticated called");
                    isValidToken = authenticateKakaoUser(accessToken);
                    break;
                case GITHUB:
                    log.info("AuthenticationFilter.doFilter GITHUB authenticated called");
                    isValidToken = authenticateGithubUser(accessToken);
                    break;
                default:  // 잘못된 Provider
                    throw new UserException(UserErrorType.PROVIDER_TYPE_ERROR);
            }
            return isValidToken;
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("AuthenticationService - Unauthorized Exception: " + e.getMessage());
            if (e.getMessage().contains("expire") || e.getMessage().contains("exist")) throw new ExpiredTokenException(ErrorCode.ACCESSTOKEN_EXPIRED);
            throw new AuthException(ErrorCode.UNAUTHORIZED, e.getMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            log.error("AuthenticationService - BadRequest Exception: " + e.getMessage());
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        } catch (HttpServerErrorException e) {
            log.error("AuthenticationService - Internal ServerError Exception: " + e.getMessage());
            throw new AuthException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean authenticateKakaoUser(String accessToken) {
        log.info("Authenticate KakaoUser : " + accessToken);
        String url = "https://kapi.kakao.com/v1/user/access_token_info";
        log.info("AuthenticationService - authenticateKakaoUser");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken); // header로 넘어오는 accessToken은 Bearer 붙어있음

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    public boolean authenticateGithubUser(String accessToken) {

        accessToken = accessToken.replace("Bearer ", "");
        log.info("Authenticate GithubUser : " + accessToken);

        String url = "https://api.github.com/applications/" + github_client_id + "/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(github_client_id, github_client_secret_key);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        String requestBody = "{\"access_token\": \"" + accessToken + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }
}
