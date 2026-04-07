package com.ratelimiter.controller;

import com.ratelimiter.model.RateLimitRule;
import com.ratelimiter.repository.RateLimitRuleRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/rules")
public class AdminController {

    private final RateLimitRuleRepository repo;

    public AdminController(RateLimitRuleRepository repo) {
        this.repo = repo;
    }

    // Register a new rule
    @PostMapping
    public RateLimitRule createRule(@RequestBody RateLimitRule rule) {
        return repo.save(rule);
    }

    // List all rules
    @GetMapping
    public List<RateLimitRule> listRules() {
        return repo.findAll();
    }

    // Update a rule (e.g. change limit from 100 to 200)
    @PutMapping("/{id}")
    public RateLimitRule updateRule(@PathVariable String id,
                                    @RequestBody RateLimitRule updated) {
        updated.setId(id);
        return repo.save(updated);
    }

    // Delete a rule
    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable String id) {
        repo.deleteById(id);
    }
}