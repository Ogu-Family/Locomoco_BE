package org.prgms.locomocoserver.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.prgms.locomocoserver.global.filter.AuthenticationFilter;
import org.prgms.locomocoserver.global.filter.CorsFilter;
import org.prgms.locomocoserver.global.filter.ExceptionHandlerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(CorsFilter filter) {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setOrder(1);
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/v1/*"); // 적절한 URL 패턴으로 변경해야 함
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> accessTokenFilterRegistration(AuthenticationFilter filter) {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setOrder(3);
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api/v1/chats/rooms/*"); // 필터를 적용할 URL 패턴 지정
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ExceptionHandlerFilter> ExceptionHandlerFilterRegistration(ExceptionHandlerFilter filter) {
        FilterRegistrationBean<ExceptionHandlerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setOrder(2);
        registrationBean.setFilter(filter);
        return registrationBean;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
