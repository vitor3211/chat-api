package com.example.demo.security;

import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {

    private final LoadingCache<String, Bucket> buckets;


    private Bucket createBucket(RateLimiter rateLimit) {
        Bandwidth limit = Bandwidth.classic(
                rateLimit.capacity(),
                Refill.intervally(rateLimit.refillTokens(), Duration.ofSeconds(rateLimit.refillDurationSeconds()))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimiter rateLimit) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if(ip == null) ip = request.getRemoteAddr();
        String methodKey = joinPoint.getSignature().toShortString();
        String key = ip + ":" + methodKey;
        Bucket bucket = buckets.get(key, k -> createBucket(rateLimit));

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            log.warn("Rate limit exceeded for IP {} on endpoint {}", ip, methodKey);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests!");
        }
    }
}
