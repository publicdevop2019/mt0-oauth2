package com.hw.shared;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class RecordTimeAspectConfig {

    @Pointcut("@annotation(com.hw.shared.RecordElapseTime)")
    public void restrictAccess() {
        //for aop purpose
    }

    @Around(value = "com.hw.shared.RecordTimeAspectConfig.restrictAccess()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        log.debug("elapse time for [class] {} [method] {} is [{}]", joinPoint.getSignature().getDeclaringType(), joinPoint.getSignature().getName(), System.currentTimeMillis() - startTime);
        return proceed;
    }
}
