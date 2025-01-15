package org.prgms.locomocoserver.global.config;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.global.interceptor.CustomHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomHandshakeInterceptor customHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/api/v1/stomp/chat")
                .addInterceptors(customHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:8090", "http://localhost:3000", "https://locomoco.kro.kr")
                .withSockJS(); // 웹소켓 핸드셰이크 커넥션 생성 경로
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /pub 으로 시작하는 destination 헤더는 @DestinationMapping 경로 이동
        config.setApplicationDestinationPrefixes("/pub");
        //  /sub 으로 시작하는 destination 헤더를 가진 메세지를 브로커로 라우팅
        config.enableSimpleBroker("/sub");
    }
}
