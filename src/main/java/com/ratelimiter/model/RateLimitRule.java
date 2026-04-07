package com.ratelimiter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rate_limit_rules")
public class RateLimitRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String routePattern; // e.g. "/api/payments/**"
    private String clientId; // IP address or API key — "*" means all
    private int maxRequests; // e.g. 100
    private int windowSeconds; // e.g. 60

    @Enumerated(EnumType.STRING)
    private AlgorithmType algorithm; // TOKEN_BUCKET or SLIDING_WINDOW

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoutePattern() {
        return routePattern;
    }

    public void setRoutePattern(String routePattern) {
        this.routePattern = routePattern;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public int getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(int windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public AlgorithmType getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmType algorithm) {
        this.algorithm = algorithm;
    }
}