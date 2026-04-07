package com.ratelimiter.algorithm;

import com.ratelimiter.model.RateLimitRule;
import com.ratelimiter.service.MetricsService;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
public class TokenBucketAlgorithm implements RateLimitAlgorithm {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final MetricsService metricsService;

    public TokenBucketAlgorithm(ReactiveRedisTemplate<String, String> redisTemplate, MetricsService metricsService) {
        this.redisTemplate = redisTemplate;
        this.metricsService = metricsService;
    }

    @Override
    public Mono<Boolean> isAllowed(String key, RateLimitRule rule) {
        String tokenKey = "bucket:tokens:" + key;
        String timeKey = "bucket:time:" + key;
        long now = System.currentTimeMillis();
        int capacity = rule.getMaxRequests();
        double refillRate = (double) capacity / rule.getWindowSeconds(); // tokens per second

        return redisTemplate.opsForValue().get(tokenKey)
                .defaultIfEmpty(String.valueOf(capacity))
                .zipWith(redisTemplate.opsForValue().get(timeKey)
                        .defaultIfEmpty(String.valueOf(now)))
                .flatMap(tuple -> {
                    double tokens = Double.parseDouble(tuple.getT1());
                    long lastRefill = Long.parseLong(tuple.getT2());
                    double elapsed = (now - lastRefill) / 1000.0;
                    // Refill tokens based on elapsed time
                    tokens = Math.min(capacity, tokens + elapsed * refillRate);

                    String clientIp = key.substring(rule.getId().length() + 1);
                    if (tokens < 1) {
                        metricsService.recordWindowUsage(rule.getRoutePattern(), clientIp, 1.0);
                        return Mono.just(false); // no tokens left
                    }
                    tokens -= 1;
                    String newTokens = String.valueOf(tokens);
                    metricsService.recordWindowUsage(rule.getRoutePattern(), clientIp, 1.0 - (tokens / capacity));
                    return redisTemplate.opsForValue().set(tokenKey, newTokens)
                            .then(redisTemplate.opsForValue().set(timeKey, String.valueOf(now)))
                            .then(redisTemplate.expire(tokenKey, Duration.ofSeconds(rule.getWindowSeconds() * 2)))
                            .thenReturn(true);
                });
    }
}