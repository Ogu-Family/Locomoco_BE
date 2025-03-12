package org.prgms.locomocoserver.global.config;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.chat.infrastructure.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/stomp/chat")
                .setAllowedOrigins("http://localhost:3000",
                        "https://locomoco.kro.kr",
                        "https://locomoco.shop")
                .withSockJS();
        registry.addEndpoint("/api/v1/stomp/chat")
                .setAllowedOrigins("http://localhost:3000",
                        "https://locomoco.kro.kr",
                        "https://locomoco.shop");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /pub 으로 시작하는 destination 헤더는 @DestinationMapping 경로 이동
        config.setApplicationDestinationPrefixes("/pub");
        //  /sub 으로 시작하는 destination 헤더를 가진 메세지를 브로커로 라우팅
        config.enableSimpleBroker("/sub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
