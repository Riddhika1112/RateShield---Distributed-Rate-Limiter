package com.ratelimiter.repository;

import com.ratelimiter.model.RateLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateLimitRuleRepository extends JpaRepository<RateLimitRule, String> {

    @Query("SELECT r FROM RateLimitRule r WHERE r.routePattern = :routePattern AND (r.clientId = :clientIp OR r.clientId = '*') ORDER BY r.clientId DESC LIMIT 1")
    Optional<RateLimitRule> findBestMatch(@Param("routePattern") String routePattern, @Param("clientIp") String clientIp);
}
