package org.prgms.locomocoserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.global.exception.ExpiredTokenException;
import org.prgms.locomocoserver.global.exception.InvalidTokenException;
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
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
            log.info("AuthenticationFilter.doFilter OPTION called");
            // preflight 요청에 대한 허용 응답 설정
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://locomoco.kro.kr");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, provider");
            httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, provider");  // 이건 있어야 하는지 확인
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (accessToken == null || providerValue == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            boolean isValidToken = authenticationService.authenticateUser(providerValue, accessToken);

            if (!isValidToken) {
                log.info("AuthenticationFilter.doFilter !isValidToken called");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        } catch (RuntimeException e) {
            log.error("AuthenticationFilter - Exception: " + e.getMessage());
            throw e; // 예외를 ExceptionHandlerFilter로 전달
        }

        chain.doFilter(request, response);
    }
}
