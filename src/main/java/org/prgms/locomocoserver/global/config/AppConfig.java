package org.prgms.locomocoserver.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        return new ObjectMapper();
    }
}
