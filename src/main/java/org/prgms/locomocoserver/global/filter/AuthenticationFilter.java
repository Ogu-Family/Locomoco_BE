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
import org.prgms.locomocoserver.user.application.AuthenticationService;
import org.prgms.locomocoserver.user.application.RefreshTokenService;
import org.prgms.locomocoserver.user.application.TokenService;
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

    private static final Set<String> allowedOrigin = new HashSet<>(Arrays.asList(
            "http://localhost:3000",
            "https://locomoco.kro.kr",
            "https://locomoco.shop",
            "http://localhost:8090"
    ));
    private static final List<String> authRequired = List.of(
            "GET:/api/v1/chats/rooms/\\d+",
            "PATCH:/api/v1/mogakko/map/\\d+",
            "GET:/api/v1/users/\\d+"
    );

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Authentication Filter Started");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        UserContext.clear();

        // Preflight 처리
        if (isPreflightRequest(httpRequest)) {
            handleCorsPreflight(httpRequest, httpResponse);
            return;
        }

        if (isAuthRequired(httpRequest)) {
            log.info("Authentication Required for {}", httpRequest.getRequestURI());
            handleAuthentication(httpRequest, httpResponse);
        }

        chain.doFilter(request, response);

    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private void handleCorsPreflight(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String origin = httpRequest.getHeader("Origin") == null ? "https://locomoco.kro.kr" : httpRequest.getHeader("Origin");

        log.info("CORS preflight request from Origin: {}", origin);
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
        } else if (Provider.KAKAO.name().equals(providerValue)) {
            processTokenRefresh(response, accessToken);
        } else {
            log.error("Authentication failed (AuthFilter): {}", ErrorCode.INVALID_TOKEN.getMessage());
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }

    private void processTokenRefresh(HttpServletResponse response, String accessToken) throws IOException {
        log.info("Refreshing access token");

        TokenResponseDto tokenResponseDto = refreshTokenService.updateAccessToken(accessToken);
        String jsonResponse = objectMapper.writeValueAsString(tokenResponseDto);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);

        log.info("New access token issued: {}", tokenResponseDto.accessToken());
    }

    private boolean isPatternMatch(String pattern, String method, String url) {
        String[] parts = pattern.split(":");
        String patternMethod = parts[0];
        String patternUrl = parts[1];

        return method.equalsIgnoreCase(patternMethod) && url.matches(patternUrl);
    }
}
