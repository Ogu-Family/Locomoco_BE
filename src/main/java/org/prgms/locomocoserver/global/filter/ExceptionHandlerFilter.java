package org.prgms.locomocoserver.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.common.aop.Logging;
import org.prgms.locomocoserver.global.common.dto.ErrorResponse;
import org.prgms.locomocoserver.global.exception.AuthException;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.global.exception.ExpiredTokenException;
import org.prgms.locomocoserver.global.exception.InvalidTokenException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends GenericFilterBean {

    @Logging
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (RuntimeException e) {
            handleException(httpServletResponse, e);
        }
    }

    @Logging
    private void handleException(HttpServletResponse response, RuntimeException e) {
        log.error("ExceptionHandlerFilter - Exception: " + e.getMessage());

        if (e instanceof ExpiredTokenException) {
            log.info("ExpiredTokenException! {}", e.getStackTrace());
            setErrorResponse(response, ErrorCode.ACCESSTOKEN_EXPIRED);
        } else if (e instanceof InvalidTokenException) {
            log.info("InvalidTokenException! {}", e.getStackTrace());
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        } else if (e instanceof AuthException) {
            log.info("AuthException! {}", e.getStackTrace());
            setErrorResponse(response, ErrorCode.UNAUTHORIZED);
        } else {
            // 기타 예외 처리
            log.info("기타 에러! {}", e.getStackTrace());
            setErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error("Error writing error response: " + e.getMessage());
        }
    }
}
