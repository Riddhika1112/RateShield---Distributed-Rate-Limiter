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
                        width: 100%;
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
                        width: 100%;
                        max-width: 300px;
                    }
                    .btn:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.5);
                    }
                    .btn:active {
                        transform: translateY(0);
                    }
                    .status {
                        margin-top: 1.5rem;
                        font-size: 0.9rem;
                        color: #cbd5e1;
                    }
                    .result-box {
                        margin-top: 2rem;
                        padding: 1.5rem;
                        border-radius: 0.5rem;
                        font-weight: bold;
                        font-family: monospace;
                        font-size: 1.2rem;
                        display: none;
                        transition: all 0.3s ease;
                    }
                    .success {
                        display: block;
                        background: rgba(34, 197, 94, 0.1);
                        color: #4ade80;
                        border: 1px solid rgba(34, 197, 94, 0.2);
                        box-shadow: 0 0 15px rgba(34, 197, 94, 0.2);
                    }
                    .ratelimit {
                        display: block;
                        background: rgba(239, 68, 68, 0.1);
                        color: #f87171;
                        border: 1px solid rgba(239, 68, 68, 0.2);
                        box-shadow: 0 0 15px rgba(239, 68, 68, 0.2);
                        animation: shake 0.4s cubic-bezier(.36,.07,.19,.97) both;
                    }
                    @keyframes shake {
                        10%, 90% { transform: translate3d(-1px, 0, 0); }
                        20%, 80% { transform: translate3d(2px, 0, 0); }
                        30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
                        40%, 60% { transform: translate3d(4px, 0, 0); }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🛡️ RateShield API</h1>
                    <p>Welcome to my Distributed Rate Limiter built with Spring Boot, Redis, and PostgreSQL! You are currently interacting with the live AWS deployment.</p>
                    
                    <button id="test-btn" class="btn">Fire Request to API</button>
                    <div class="status">Click rapidly to exhaust the Token Bucket limit! (5 reqs/min)</div>

                    <div id="result" class="result-box"></div>
                </div>

                <script>
                    const btn = document.getElementById('test-btn');
                    const resultBox = document.getElementById('result');

                    btn.addEventListener('click', async () => {
                        // Reset animation state
                        resultBox.className = 'result-box';
                        resultBox.innerText = 'Routing request...';
                        resultBox.style.display = 'block';

                        try {
                            const startTime = performance.now();
                            const response = await fetch('/proxy/get');
                            const endTime = performance.now();
                            const ms = Math.round(endTime - startTime);

                            if (response.status === 200) {
                                resultBox.className = 'result-box success';
                                resultBox.innerHTML = `✅ HTTP 200 OK<br><span style="font-size:0.85rem; color:#94a3b8; font-weight:normal">Request passed through firewall in ${ms}ms</span>`;
                            } else if (response.status === 429) {
                                resultBox.className = 'result-box ratelimit';
                                resultBox.innerHTML = `🛑 HTTP 429 TOO MANY REQUESTS<br><span style="font-size:0.85rem; color:#94a3b8; font-weight:normal">Request blocked by Redis Rate Limiter in ${ms}ms</span>`;
                            } else {
                                resultBox.className = 'result-box';
                                resultBox.innerHTML = `⚠️ HTTP ${response.status} Unknown`;
                            }
                        } catch (e) {
                            resultBox.className = 'result-box ratelimit';
                            resultBox.innerHTML = `❌ Connection Error`;
                        }
                    });
                </script>
            </body>
            </html>
            """;
    }
}
