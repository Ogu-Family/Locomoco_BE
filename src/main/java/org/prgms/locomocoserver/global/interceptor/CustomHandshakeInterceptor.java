package org.prgms.locomocoserver.global.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.user.application.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            String query = servletRequest.getServletRequest().getQueryString();
            if (query == null || query.isEmpty()) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }

            Map<String, String> queryParams = parseQueryParams(query);
            String accessToken = queryParams.get("accessToken");
            String provider = queryParams.get("provider");
            log.info("access token: " + accessToken + ", provider: " + provider);

            if (accessToken == null || provider == null) {
                response.setStatusCode(HttpStatus.BAD_REQUEST); // 필수 파라미터 누락
                return false;
            }

            boolean isValidToken = tokenService.isValidToken(accessToken, provider);
            if (isValidToken) {
                attributes.put("user", tokenService.getUserFromToken(accessToken.substring(7), provider));
                return true;
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED); // 인증 실패
                return false;
            }
        }

        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return queryParams;
    }
}

