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
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

    private final AuthenticationService authenticationService;
    private static HashSet<String> alloweOrigin = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        alloweOrigin.add("http://localhost:3000");
        alloweOrigin.add("https://locomoco.kro.kr");
        alloweOrigin.add("https://locomoco.shop");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String accessToken = httpRequest.getHeader("Authorization");
        String providerValue = httpRequest.getHeader("provider");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            String origin = httpRequest.getHeader("Origin") == null ? "https://locomoco.kro.kr" : httpRequest.getHeader("Origin");
            log.info("CorsFilter Origin : " + origin);
            if(alloweOrigin.contains(origin) == false) return;
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
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
