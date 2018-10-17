package com.github.service.accountservice.aspect;

import com.github.service.accountservice.enums.ErrorCode;
import com.github.service.accountservice.enums.ErrorMessage;
import com.github.service.accountservice.exceptions.RateLimiterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;
    Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Pointcut("@annotation(rateLimit)")
    private void annotatedWithRateLimit(RateLimit rateLimit) {}

    @Pointcut("@within(org.springframework.stereotype.Controller)"
            + " || @within(org.springframework.web.bind.annotation.RestController)")
    private void controllerMethods() {}

    @Before("controllerMethods() && annotatedWithRateLimit(rateLimit)")
    public void rateLimitProcess(final JoinPoint joinPoint,
                                 RateLimit rateLimit) throws RateLimiterException {

        logger.info("RateLimitProcess started...");

        HttpServletRequest request = getRequest(joinPoint.getArgs());

        if (request == null) {
            logger.error(ErrorMessage.REQUEST_NOT_FOUND.getMessage());
            return;
        }

        String ip = request.getRemoteHost();
        String url = request.getRequestURI();
        String key = String.format("req:lim:%s:%s", url, ip);
        long count = redisTemplate.opsForValue().increment(key, 1);

        logger.debug("[Redis] {} = {}", key, count);

        if (count == 1) {
            redisTemplate.expire(key, rateLimit.duration(), rateLimit.unit());
        }
        if (count > rateLimit.limit()) {
            log.warn("Ip : {}, Try count : {}, url : {}, rateLimit : {}", ip, count, url, rateLimit.limit());
            throw new RateLimiterException(ErrorMessage.RATE_LIMITER_FLOW.getMessage(), ErrorCode.Too_many_request.getCode());
        }
    }

    private HttpServletRequest getRequest(Object[] args) {

        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                return (HttpServletRequest)arg;
            }
        }
        return null;
    }
}