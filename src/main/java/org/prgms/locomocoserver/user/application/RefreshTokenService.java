package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.user.domain.RefreshToken;
import org.prgms.locomocoserver.user.domain.RefreshTokenRepository;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${oauth.kakao.REST_API_KEY}")
    private String kakao_api_key;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String saveTokenInfo(TokenResponseDto tokenResponseDto) {
        RefreshToken refreshToken = refreshTokenRepository.save(tokenResponseDto.toRefreshTokenEntity());
        return refreshToken.getAccessToken();
    }

    @Transactional
    public void removeAccessToken(String accessToken) {
        RefreshToken refreshToken = getByAccessToken(accessToken);
        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional
    public TokenResponseDto updateAccessToken(String accessToken) {
        RefreshToken refreshToken = getByAccessToken(accessToken);

        TokenResponseDto tokenResponseDto = refreshKakaoAccessToken(refreshToken.getRefreshToken());

        saveTokenInfo(tokenResponseDto);
        removeAccessToken(accessToken);

        return tokenResponseDto;
    }

    private RefreshToken getByAccessToken(String accessToken) {
        return refreshTokenRepository.findByAccessToken(accessToken)
               .orElseThrow(() -> new UserException(UserErrorType.ACCESSTOKEN_NOT_FOUND));
    }

    private TokenResponseDto refreshKakaoAccessToken(String refreshToken) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", kakao_api_key);
        params.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TokenResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, TokenResponseDto.class);

        return response.getBody();
    }
}
