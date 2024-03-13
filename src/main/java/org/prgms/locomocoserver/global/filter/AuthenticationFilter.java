package org.prgms.locomocoserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.exception.ExpiredTokenException;
import org.prgms.locomocoserver.global.exception.InvalidTokenException;
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

    private final AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String accessToken = httpRequest.getHeader("Authorization");
        String providerValue = httpRequest.getHeader("provider");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            // preflight 요청에 대한 허용 응답 설정
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, provider");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, provider");  // 이건 있어야 하는지 확인
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (accessToken == null || providerValue == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Provider provider = Provider.valueOf(providerValue.toUpperCase());
        try {
            boolean isValidToken = false;
            switch (provider) {
                case KAKAO:
                    isValidToken = authenticationService.authenticateKakaoUser(accessToken);
                    break;
                case GITHUB:
                    isValidToken = authenticationService.authenticateGithubUser(accessToken);
                    break;
                default:  // 잘못된 Provider
                    throw new UserException(UserErrorType.PROVIDER_TYPE_ERROR);
            }

            if (!isValidToken) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            chain.doFilter(request, response);
        } catch (ExpiredTokenException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (InvalidTokenException e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
