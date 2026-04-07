package com.ratelimiter.filter;

import com.ratelimiter.service.RateLimitService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitGatewayFilter implements GlobalFilter, Ordered {

    private final RateLimitService rateLimitService;

    public RateLimitGatewayFilter(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        String routePath = exchange.getRequest().getPath().value();

        return rateLimitService.isAllowed(clientIp, routePath)
                .flatMap(allowed -> {
                    if (!allowed) {
                        // Return 429 Too Many Requests
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        response.getHeaders().add("Retry-After", "60");
                        response.getHeaders().add("X-RateLimit-Limit", "see /admin/rules");
                        return response.setComplete();
                    }
                    // Allowed — pass request through to the downstream service
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -1; // Run before all other filters
    }
}
