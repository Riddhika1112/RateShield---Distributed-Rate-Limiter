package com.ratelimiter.algorithm;

import com.ratelimiter.model.RateLimitRule;
import com.ratelimiter.service.MetricsService;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
public class SlidingWindowAlgorithm implements RateLimitAlgorithm {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final MetricsService metricsService;

    public SlidingWindowAlgorithm(ReactiveRedisTemplate<String, String> redisTemplate, MetricsService metricsService) {
        this.redisTemplate = redisTemplate;
        this.metricsService = metricsService;
    }

    @Override
    public Mono<Boolean> isAllowed(String key, RateLimitRule rule) {
        long now = System.currentTimeMillis();
        long windowStart = now - (rule.getWindowSeconds() * 1000L);
        String redisKey = "sliding:" + key;

        return redisTemplate.opsForZSet()
                // Step 1: remove timestamps older than the window
                .removeRangeByScore(redisKey, org.springframework.data.domain.Range.closed(0.0, (double) windowStart))
                .then(
                        // Step 2: count how many requests remain in the window
                        redisTemplate.opsForZSet().count(redisKey, org.springframework.data.domain.Range.closed((double) windowStart, (double) now)))
                .flatMap(count -> {
                    String clientIp = key.substring(rule.getId().length() + 1);
                    double usage = Math.min(1.0, (double) count / rule.getMaxRequests());
                    metricsService.recordWindowUsage(rule.getRoutePattern(), clientIp, usage);

                    if (count >= rule.getMaxRequests()) {
                        // Limit exceeded — reject
                        return Mono.just(false);
                    }
                    // Step 3: record this request (score = timestamp, value = unique id)
                    return redisTemplate.opsForZSet()
                            .add(redisKey, now + "-" + Math.random(), now)
                            .then(redisTemplate.expire(redisKey,
                                    Duration.ofSeconds(rule.getWindowSeconds())))
                            .thenReturn(true);
                });
    }
}
