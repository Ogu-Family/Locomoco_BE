package org.prgms.locomocoserver.global.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.global.annotation.GetUser;
import org.prgms.locomocoserver.global.context.UserContext;
import org.prgms.locomocoserver.user.domain.User;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpServletRequest httpServletRequest;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 파라미터가 @GetUser 어노테이션이 붙어 있고, 타입이 User인 경우
        return parameter.getParameterAnnotation(GetUser.class) != null
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // UserContext에서 현재 사용자를 가져옴
        User user = UserContext.getUser();
        if (user == null) {
            log.error("User Not Found in UserContext");
            throw new UserException(UserErrorType.USER_NOT_FOUND);
        }
        return user;
    }
}
