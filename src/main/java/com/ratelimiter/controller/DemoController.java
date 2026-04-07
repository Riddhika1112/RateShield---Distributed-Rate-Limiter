package com.ratelimiter.controller;

import com.ratelimiter.service.MetricsService;
import com.ratelimiter.service.RateLimitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final RateLimitService rateLimitService;
    private final MetricsService metricsService;

    public DemoController(RateLimitService rateLimitService, MetricsService metricsService) {
        this.rateLimitService = rateLimitService;
        this.metricsService = metricsService;
    }

    @GetMapping("/hit")
    public Mono<Map<String, Object>> hitDemo(
            @RequestParam(defaultValue = "https://example.com") String target,
            @RequestParam(defaultValue = "20") int times) {
        
        // We simulate a request by checking rateLimitService internally N times
        return Flux.range(1, times)
                // Use concatMap to execute them sequentially so rate limits are evaluated in order
                .concatMap(i -> rateLimitService.isAllowed("127.0.0.1", "/demo/hit"))
                .collectList()
                .map(results -> {
                    long allowed = results.stream().filter(b -> b).count();
                    long blocked = results.stream().filter(b -> !b).count();

                    Map<String, Object> response = new HashMap<>();
                    response.put("allowed", allowed);
                    response.put("blocked", blocked);
                    response.put("route", "/demo/hit");
                    return response;
                });
    }

    @GetMapping("/status")
    public Map<String, Double> status() {
        // Return current usage percentages
        return metricsService.getWindowUsageMap();
    }
}
