package org.prgms.locomocoserver.global.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
    @Pointcut("@annotation(Logging)")
    private void loggingPointcut() {}

    @Around("loggingPointcut()")
    private Object logging(ProceedingJoinPoint pjp) {
        Object ret;
        log.info("{} is started.", pjp.getTarget().getClass().getSimpleName());
        try {
            ret = pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        log.info("{} is ended.", pjp.getTarget().getClass().getSimpleName());

        return ret;
    }
}
