package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.exception.ExpiredTokenException;
import org.prgms.locomocoserver.global.exception.InvalidTokenException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public boolean authenticateKakaoUser(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/access_token_info";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken); // header로 넘어오는 accessToken은 Bearer 붙어있음

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ExpiredTokenException("Kakao access token expired");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new InvalidTokenException("Invalid Kakao access token");
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Error occurred during Kakao authentication", e);
        }
    }

    public boolean authenticateGithubUser(String accessToken) {
        String url = "https://api.github.com/applications/Iv1.8a61f9b3a7aba766/token";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            headers.set("Accept", "application/vnd.github+json");
            headers.set("X-GitHub-Api-Version", "2022-11-28");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ExpiredTokenException("GitHub access token expired");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new InvalidTokenException("Invalid GitHub access token");
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Error occurred during GitHub authentication", e);
        }
    }
}
