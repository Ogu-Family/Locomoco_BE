package org.prgms.locomocoserver.global.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.prgms.locomocoserver.global.annotation.Authenticated;
import org.prgms.locomocoserver.global.context.UserContext;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.UserRepository;
import org.prgms.locomocoserver.user.dto.OAuthUserInfoDto;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.prgms.locomocoserver.user.presentation.GithubController;
import org.prgms.locomocoserver.user.presentation.KakaoController;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

    private final HttpServletRequest httpServletRequest;
    private final KakaoController kakaoController;
    private final GithubController githubController;
    private final UserRepository userRepository;

    @Pointcut("@annotation(authenticated)")
    public void authPointcut(Authenticated authenticated) {
    }

    @Before("authPointcut(authenticated)")
    public void beforeMethod(JoinPoint joinPoint, Authenticated authenticated) throws JsonProcessingException {
        String accessToken = httpServletRequest.getHeader("Authorization");
        String providerValue = httpServletRequest.getHeader("provider");

        // filter 에서 토큰 유효성 검사
        // 엑세스 토큰을 통해 사용자 엔티티 가져오기
        OAuthUserInfoDto userInfoDto;
        switch (providerValue) {
            case "KAKAO":
                userInfoDto = kakaoController.loadUserInfo(accessToken);
                break;
            case "GITHUB":
                userInfoDto = githubController.loadUserInfo(accessToken);
                break;
            default:
                throw new UserException(UserErrorType.PROVIDER_TYPE_ERROR);
        }

        User user = userRepository.findByEmailAndProviderAndDeletedAtIsNull(userInfoDto.getEmail(), userInfoDto.getProvider())
                .orElseThrow(() -> new UserException(UserErrorType.USER_NOT_FOUND));
        UserContext.setUser(user);
        log.info("Authentication success for {}", user.getNickname());
    }

}
