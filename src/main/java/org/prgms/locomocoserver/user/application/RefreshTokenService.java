package org.prgms.locomocoserver.user.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.user.domain.RefreshToken;
import org.prgms.locomocoserver.user.domain.RefreshTokenRepository;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
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
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${oauth.kakao.REST_API_KEY}")
    private String kakao_api_key;

    @Transactional
    public String saveTokenInfo(TokenResponseDto tokenResponseDto) {
        RefreshToken refreshToken = refreshTokenRepository.save(tokenResponseDto.toRefreshTokenEntity());
        return refreshToken.getAccessToken();
    }

    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    @Transactional
    public TokenResponseDto updateAccessToken(String refreshToken) {
        RefreshToken findToken = getByRefreshToken(refreshToken);

        TokenResponseDto tokenResponseDto = refreshKakaoAccessToken(findToken.getRefreshToken());

        saveTokenInfo(tokenResponseDto);
        removeRefreshToken(refreshToken);

        return tokenResponseDto;
    }

    private RefreshToken getByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESSTOKEN_EXPIRED));
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
