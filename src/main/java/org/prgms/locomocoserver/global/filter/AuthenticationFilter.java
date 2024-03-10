package org.prgms.locomocoserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

    private final AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String accessToken = httpRequest.getHeader("accessToken");
        String providerValue = httpRequest.getHeader("provider");

        if (accessToken != null && providerValue != null) {

            Provider provider = Provider.valueOf(providerValue.toUpperCase());

            boolean isValidToken = false;
            switch (provider) {
                case KAKAO:
                    isValidToken = authenticationService.authenticateKakaoUser(accessToken);
                    break;
                case GITHUB:
                    isValidToken = authenticationService.authenticateGithubUser(accessToken);
                    break;
                default:  // 잘못된 Provider
                    throw new RuntimeException("해당 provider가 존재하지 않습니다.");
            }

            if (isValidToken) {  // 유효한 accessToken
                chain.doFilter(request, response);
            } else {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } else { // accessToken이 헤더에 없을 경우
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
