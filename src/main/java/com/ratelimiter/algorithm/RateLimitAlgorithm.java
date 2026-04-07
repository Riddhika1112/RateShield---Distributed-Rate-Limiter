package com.ratelimiter.algorithm;

import com.ratelimiter.model.RateLimitRule;
import reactor.core.publisher.Mono;

public interface RateLimitAlgorithm {
    /**
     * Returns true if the request is ALLOWED.
     * Increments the counter in Redis atomically.
     */
    Mono<Boolean> isAllowed(String key, RateLimitRule rule);
}