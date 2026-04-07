# Rate Limiter Middleware

A reactive Spring Boot application providing rate limiting via Spring Cloud Gateway.

## Quickstart

1. Start the observability stack and databases:
   ```bash
   docker-compose up -d
   ```
2. Start the Spring application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Create a test rate limiting rule (e.g., max 5 requests per 60s for /demo/hit):
   ```bash
   curl -X POST http://localhost:8080/admin/rules \
   -H "Content-Type: application/json" \
   -d '{"routePattern": "/demo/hit", "clientId": "*", "maxRequests": 5, "windowSeconds": 60, "algorithm": "TOKEN_BUCKET"}'
   ```
4. Check out the observability dashboard:
   - Open **http://localhost:3000** in your browser.
   - Login with `admin` / `admin`.
   - Open the auto-provisioned **"Rate Limiter"** dashboard.
5. Trigger some traffic!
   - Open a new tab and hit **http://localhost:8080/demo/hit?target=test&times=20**
   - You should see the response block traffic, e.g., `{"route":"/demo/hit","allowed":5,"blocked":15}`
   - Go back to Grafana. *You should see the blocked requests spike here!*

![Grafana Spike Placeholder](https://via.placeholder.com/800x400.png?text=Grafana+Dashboard+Blocked+Requests+Spike)
