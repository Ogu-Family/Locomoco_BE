package org.prgms.locomocoserver.user.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.prgms.locomocoserver.user.presentation.GithubController;
import org.prgms.locomocoserver.user.presentation.KakaoController;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final KakaoController kakaoController;
    private final GithubController githubController;
    private final UserRepository userRepository;

    public User getUserFromToken(String token, String providerValue) throws JsonProcessingException {
        OAuthUserInfoDto userInfoDto;
        switch (providerValue) {
            case "KAKAO":
                userInfoDto = kakaoController.loadUserInfo(token);
                log.info("TokenService - kakao : kakaoUserInfo - " + userInfoDto.getEmail());
                break;
            case "GITHUB":
                userInfoDto = githubController.loadUserInfo(token);
                log.info("TokenService - github : githubUserInfo - " + userInfoDto.getEmail());
                break;
            default:
                throw new UserException(UserErrorType.PROVIDER_TYPE_ERROR);
        }

        return userRepository.findByEmailAndProviderAndDeletedAtIsNull(userInfoDto.getEmail(), userInfoDto.getProvider())
                .orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND));
    }

}

