package com.ratelimiter.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, Double> windowUsageMap = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordRequest(String clientIp, String routePath, String status, String algorithm) {
        meterRegistry.counter("rate_limiter_requests_total",
                "route", routePath,
                "clientIp", clientIp,
                "status", status).increment();

        if ("BLOCKED".equals(status)) {
            meterRegistry.counter("rate_limiter_blocked_total",
                    "route", routePath,
                    "algorithm", algorithm != null ? algorithm : "UNKNOWN").increment();
        }
    }

    public void recordWindowUsage(String routePath, String clientIp, double usagePct) {
        String key = routePath + ":" + clientIp;
        meterRegistry.gauge("rate_limiter_redis_window_usage",
                Tags.of("route", routePath, "clientIp", clientIp),
                windowUsageMap,
                map -> map.getOrDefault(key, 0.0));
        
        windowUsageMap.put(key, usagePct);
    }

    public Map<String, Double> getWindowUsageMap() {
        return windowUsageMap;
    }
}
