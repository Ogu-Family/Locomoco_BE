package org.prgms.locomocoserver.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.context.UserContext;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.global.property.AuthProperties;
import org.prgms.locomocoserver.global.property.CorsProperties;
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.prgms.locomocoserver.user.application.RefreshTokenService;
import org.prgms.locomocoserver.user.application.TokenService;
import org.prgms.locomocoserver.user.domain.RefreshToken;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.domain.enums.Provider;
import org.prgms.locomocoserver.user.dto.response.TokenResponseDto;
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

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final CorsProperties corsProperties;
    private final AuthProperties authProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Authentication Filter Started");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Preflight 처리
            if (isPreflightRequest(httpRequest)) {
                handleCorsPreflight(httpRequest, httpResponse);
                return;
            }

            if (isAuthRequired(httpRequest)) {
                log.info("Authentication Required for {}", httpRequest.getRequestURI());
                handleAuthentication(httpRequest, httpResponse);
            }
        } finally {
            UserContext.clear();
            log.info("Authentication Filter Ended");
        }

        chain.doFilter(request, response);

    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private void handleCorsPreflight(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String origin = httpRequest.getHeader("Origin") == null ? "https://locomoco.kro.kr" : httpRequest.getHeader("Origin");

        log.info("CORS preflight request from Origin: {}", origin);
        Set<String> allowedOrigin = corsProperties.getAllowedOrigins();
        if (!allowedOrigin.contains(origin)) return;

        httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, provider");
        httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization, provider");
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isAuthRequired(HttpServletRequest request) {
        String method = request.getMethod();
        String url = request.getRequestURI();
        List<String> authRequired = authProperties.getAuthRequired();
        return authRequired.stream()
                .anyMatch(pattern -> isPatternMatch(pattern, method, url));
    }

    private void handleAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = request.getHeader("Authorization");
        String providerValue = request.getHeader("provider");

        if (accessToken == null || providerValue == null) {
            log.error("Authentication failed: {}", ErrorCode.NO_ACCESS_TOKEN.getMessage());
            throw new AuthException(ErrorCode.NO_ACCESS_TOKEN);
        }

        boolean isValidToken = authenticationService.authenticateUser(providerValue, accessToken);

        if (isValidToken) {
            User user = tokenService.getUserFromToken(accessToken.substring(7), providerValue);
            UserContext.setUser(user);
            log.info("User Context: {}", user.getEmail());
        } else {
            log.error("Authentication failed (AuthFilter): {}", ErrorCode.INVALID_TOKEN.getMessage());
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    private boolean isPatternMatch(String pattern, String method, String url) {
        String[] parts = pattern.split(":");
        String patternMethod = parts[0];
        String patternUrl = parts[1];

        return method.equalsIgnoreCase(patternMethod) && url.matches(patternUrl);
    }
}
