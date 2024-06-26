package org.prgms.locomocoserver.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

    private static final Set<String> allowedOrigin = new HashSet<>(Arrays.asList(
            "http://localhost:3000",
            "https://locomoco.kro.kr",
            "https://locomoco.shop",
            "http://localhost:8090"
    ));
    private static final List<String> authRequired = List.of(
            "GET:/api/v1/chats/rooms/\\d+",
            "PATCH:/api/v1/mogakko/map/\\d+"
    );
    private final AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            handleOptionCors(httpRequest, httpResponse);
            return;
        }

        String method = httpRequest.getMethod();
        String url = httpRequest.getRequestURI();

        if (authRequired.stream().anyMatch(pattern -> isPatternMatch(pattern, method, url))) {
            String accessToken = httpRequest.getHeader("Authorization");
            String providerValue = httpRequest.getHeader("provider");

            if (accessToken == null || providerValue == null) {
                log.error("AuthenticationFilter - Exception: " + ErrorCode.NO_ACCESS_TOKEN.getMessage());
                throw new AuthException(ErrorCode.NO_ACCESS_TOKEN);
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
        }

        chain.doFilter(request, response);
    }

    private void handleOptionCors(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String origin = httpRequest.getHeader("Origin") == null ? "https://locomoco.kro.kr" : httpRequest.getHeader("Origin");
        log.info("CorsFilter Origin : " + origin);
        if (!allowedOrigin.contains(origin)) return;
        httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, provider");
        httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, provider");
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isPatternMatch(String pattern, String method, String url) {
        String[] parts = pattern.split(":");
        String patternMethod = parts[0];
        String patternUrl = parts[1];

        return method.equalsIgnoreCase(patternMethod) && url.matches(patternUrl);
    }
}
