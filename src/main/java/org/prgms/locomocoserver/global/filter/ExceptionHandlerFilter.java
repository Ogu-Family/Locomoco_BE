package org.prgms.locomocoserver.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.exception.ErrorCode;
import org.prgms.locomocoserver.global.exception.ExpiredTokenException;
import org.prgms.locomocoserver.global.exception.InvalidTokenException;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Order(3)
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("ExceptionHandlerFilter.doFilterInternal called");
        try{
            filterChain.doFilter(request, response);
        }catch (ExpiredTokenException e){
            //토큰의 유효기간 만료
            log.info("ExceptionHandlerFilter - ExpiredTokenException: " + e.getMessage());
            setErrorResponse(response, ErrorCode.ACCESSTOKEN_EXPIRED);
        }catch (InvalidTokenException | IllegalArgumentException e){
            //유효하지 않은 토큰
            log.info("ExceptionHandlerFilter - InvalidTokenException: " + e.getMessage());
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
    }
    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
        try{
            log.info("response.getwrite : " + objectMapper.writeValueAsString(errorResponse));
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Data
    public static class ErrorResponse{
        private final Integer code;
        private final String message;
    }
}
