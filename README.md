# 🛡️ RateShield: Distributed API Rate Limiter
<div align="center">
  <p>A high-performance, distributed rate-limiting API Gateway built to safeguard microservices against abusive traffic and DDoS attacks. Features real-time observability and dynamic routing.</p>
</div>

---

## ⚡ Features
- **Distributed State:** Utilizes Redis to accurately maintain request counts across multiple load-balanced gateway instances.
- **Multiple Algorithms:** Strictly enforces limits using scalable `TOKEN_BUCKET` or `SLIDING_WINDOW` algorithms.
- **Real-Time Observability:** Built-in Prometheus metrics and a custom Grafana dashboard for live traffic monitoring and block-rate analytics.
- **Dynamic Rules:** Manage rate-limiting rules (by IP, Route, or algorithm) on the fly via Admin APIs backed by PostgreSQL.
- **Privacy First:** Client IP addresses (PII) are masked before telemetry is sent to observability dashboards.
- **Interactive Web UI:** Includes a dynamic AJAX-based testing suite at the root directory to instantly visualize HTTP 429 rejections.
- **Cloud Ready:** Fully containerized via Docker Compose for easy scale-out on AWS EC2 or local deployment.

---

## 🏗️ Architecture Stack

| Layer | Technology | Purpose |
| ----------- | ----------- | ----------- |
| **Gateway / Core** | Java 21, Spring Boot 3 | Highly concurrent API Gateway and Filter Chain logic |
| **Caching** | Redis (Alpine) | Blazing-fast distributed state maintenance for rate algorithms |
| **Database** | PostgreSQL | Persistent storage for dynamic Rate Limit Rules |
| **Observability** | Prometheus, Micrometer | Time-series data scrapping for gateway request logs |
| **Visualization** | Grafana | Beautiful dashboards graphing `total vs blocked` requests |
| **DevOps** | Docker, Docker Compose | Painless, one-command deployment orchestration |

---

## 🚀 Quick Start (Docker Compose)

The easiest way to get RateShield running is via Docker. Ensure you have Docker and Docker Compose (v2) installed.

### 1. Fire up the cluster
```bash
docker compose up -d --build
```
*This command orchestrates the entire cluster: Spring Boot App, Redis, PostgreSQL, Prometheus, and Grafana.*

### 2. Verify Deployment
Once started, the system exposes three primary URLs:
* **Interactive UI:** [http://localhost:8080/](http://localhost:8080/)
* **Grafana Dashboard:** [http://localhost:3000/](http://localhost:3000/) *(Username/Password: admin / admin)*
* **Prometheus Targets:** [http://localhost:9090/](http://localhost:9090/)

---

## 📖 Usage & API Documentation

By default, RateShield acts as a passthrough, strictly allowing traffic until it hits a rule defined in PostgreSQL.

### 1. Create a Rate Limit Rule
Let's restrict the `/proxy/**` route to exactly **5 requests per minute** using the Token Bucket system.

```bash
curl -X POST http://localhost:8080/admin/rules \
-H "Content-Type: application/json" \
-d '{
  "routePattern": "/proxy/**",
  "clientId": "*",
  "maxRequests": 5,
  "windowSeconds": 60,
  "algorithm": "TOKEN_BUCKET"
}'
```

### 2. Test the Limiter
Blast the endpoint with 10 requests. The first 5 will pass through with a `200 OK`, and the remaining 5 will be stopped at the gateway edge with exactly a `429 Too Many Requests`. 

```bash
for i in {1..10}; do \
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/proxy/get; \
done
```

> Alternatively, visit [http://localhost:8080/](http://localhost:8080/) in your browser and rapidly click the Test button!

### 3. Check Observability
Log in to **Grafana** (`http://localhost:3000`), open the pre-provisioned **Rate Limiter Dashboard**, and watch the yellow `BLOCKED` traffic spike in real-time.

---

## 📂 Project Structure
```text
├── src/main/java/com/ratelimiter/
│   ├── algorithm/     # Core mathematical logic for Sliding Window / Token Bucket
│   ├── config/        # Component configurations (Redis, Routes)
│   ├── controller/    # Admin APIs and Interactive UI
│   ├── filter/        # Spring Cloud Gateway interception chain
│   ├── model/         # PostgreSQL JPA Entities
│   ├── service/       # Prometheus Micrometer bindings and logic
├── grafana/
│   ├── dashboards/    # Exported JSON Grafana Visuals (PromQL)
│   ├── provisioning/  # Auto-binds Prometheus as a Data Source
├── docker-compose.yml # Orchestration definition
├── Dockerfile         # Multi-stage lightweight Java 21 build
```

---

*Built with ❤️ to demonstrate battle-tested backend architecture and real-world system resilience.*
