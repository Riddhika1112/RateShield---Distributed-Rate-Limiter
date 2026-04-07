package com.ratelimiter.service;

import com.ratelimiter.algorithm.SlidingWindowAlgorithm;
import com.ratelimiter.algorithm.TokenBucketAlgorithm;

import com.ratelimiter.repository.RateLimitRuleRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RateLimitService {

    private final RateLimitRuleRepository ruleRepo;
    private final TokenBucketAlgorithm tokenBucket;
    private final SlidingWindowAlgorithm slidingWindow;
    private final MetricsService metricsService;

    public RateLimitService(RateLimitRuleRepository ruleRepo, TokenBucketAlgorithm tokenBucket, SlidingWindowAlgorithm slidingWindow, MetricsService metricsService) {
        this.ruleRepo = ruleRepo;
        this.tokenBucket = tokenBucket;
        this.slidingWindow = slidingWindow;
        this.metricsService = metricsService;
    }

    public Mono<Boolean> isAllowed(String clientIp, String routePath) {
        // Find the most specific rule matching this route and client
        return Mono.fromCallable(() ->
                ruleRepo.findBestMatch(routePath, clientIp)  // custom query
                        .orElse(null)
        ).flatMap(rule -> {
            if (rule == null) {
                metricsService.recordRequest(clientIp, routePath, "ALLOWED", "NONE");
                return Mono.just(true); // no rule = allow
            }

            String redisKey = rule.getId() + ":" + clientIp;
            Mono<Boolean> allowedMono = switch (rule.getAlgorithm()) {
                case TOKEN_BUCKET    -> tokenBucket.isAllowed(redisKey, rule);
                case SLIDING_WINDOW  -> slidingWindow.isAllowed(redisKey, rule);
            };

            return allowedMono.doOnNext(allowed -> {
                String status = allowed ? "ALLOWED" : "BLOCKED";
                metricsService.recordRequest(clientIp, routePath, status, rule.getAlgorithm().name());
            });
        });
    }
}