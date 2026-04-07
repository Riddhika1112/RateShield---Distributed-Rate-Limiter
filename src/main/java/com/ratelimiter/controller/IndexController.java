package com.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping(value = "/", produces = "text/html")
    public String index() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>RateShield API Gateway</title>
                <style>
                    body {
                        font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
                        color: #f8fafc;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        height: 100vh;
                        margin: 0;
                    }
                    .container {
                        text-align: center;
                        background: rgba(255, 255, 255, 0.05);
                        padding: 3rem;
                        border-radius: 1rem;
                        box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
                        backdrop-filter: blur(10px);
                        border: 1px solid rgba(255, 255, 255, 0.1);
                        max-width: 600px;
                    }
                    h1 {
                        font-size: 2.5rem;
                        margin-bottom: 1rem;
                        background: linear-gradient(to right, #38bdf8, #818cf8);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                    }
                    p {
                        font-size: 1.1rem;
                        color: #94a3b8;
                        line-height: 1.6;
                        margin-bottom: 2rem;
                    }
                    .btn {
                        display: inline-block;
                        padding: 0.8rem 2rem;
                        font-size: 1.1rem;
                        font-weight: 600;
                        color: #fff;
                        background: linear-gradient(to right, #3b82f6, #6366f1);
                        border: none;
                        border-radius: 0.5rem;
                        cursor: pointer;
                        text-decoration: none;
                        transition: transform 0.2s, box-shadow 0.2s;
                    }
                    .btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.5);
                    }
                    .btn:active {
                        transform: translateY(0);
                    }
                    .status {
                        margin-top: 2rem;
                        font-size: 0.9rem;
                        color: #cbd5e1;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🛡️ RateShield API</h1>
                    <p>Welcome to my Distributed Rate Limiter built with Spring Boot, Redis, and PostgreSQL! You are currently interacting with the live AWS deployment.</p>
                    <a href="/proxy/get" target="_blank" class="btn">Test API Endpoint</a>
                    <div class="status">Clicking this will route you through the API Gateway.<br>Refresh it fast to trigger the HTTP 429 Rate Limit!</div>
                </div>
            </body>
            </html>
            """;
    }
}
