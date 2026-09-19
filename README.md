# Bank Microservices Starter — Plumbing Only

This is the infrastructure layer for the bank microservices learning project:
**Discovery Server (Eureka) → Config Server → API Gateway**.

No domain logic lives here on purpose. Once this trio runs cleanly, you build
`auth-service`, `customer-service`, `employee-service`, `account-service`, and
`loan-service` as separate Spring Boot apps that plug into it.

Versions used: **Spring Boot 4.1.1**, **Spring Cloud 2025.1.2 (Oakwood)**, **Java 17+**.
Check for newer patch releases before you start — these move on a ~6 month cadence.

## Structure

```
bank-microservices-starter/
├── pom.xml                     <- parent POM (Maven multi-module, shares versions)
├── discovery-server/           <- Eureka registry, port 8761
├── config-server/              <- serves config to every other service, port 8888
│   └── .../resources/config-repo/
│       ├── auth-service.yml
│       ├── customer-service.yml
│       ├── account-service.yml
│       ├── loan-service.yml
│       └── employee-service.yml
└── api-gateway/                <- single entry point for clients, port 8080
```

## Run order (this matters)

1. **discovery-server** — `cd discovery-server && ../mvnw spring-boot:run` (or run the
   `DiscoveryServerApplication` class in your IDE). Check the dashboard at
   http://localhost:8761 — it should be empty at first.
2. **config-server** — same pattern. Sanity check:
   http://localhost:8888/customer-service/default should return the contents of
   `customer-service.yml` as JSON.
3. **api-gateway** — same pattern. It will show up in the Eureka dashboard within
   ~30 seconds of starting.

(You'll need Maven; if you don't have the wrapper jars, run `mvn -N io.takari:maven:wrapper`
once at the root, or just use your own installed `mvn`.)

## Wiring your first real service (auth-service) into this

When you create `auth-service` as a new Spring Boot module, it needs:

**Dependencies** (in addition to your usual web/JPA/security starters):
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

**A minimal `application.yml`** — just enough to know its own name and where the
config server lives; everything else (port, datasource, JWT secret) gets pulled
from `config-repo/auth-service.yml`:
```yaml
spring:
  application:
    name: auth-service
  config:
    import: optional:configserver:http://localhost:8888
```

That's it. Start `auth-service` after the three plumbing apps are up, and:
- It registers itself in Eureka as `AUTH-SERVICE`.
- It pulls port/datasource/JWT settings from the config server.
- The gateway route `Path=/api/auth/**` (already defined in `api-gateway`'s
  `application.yml`) will forward matching requests to it automatically —
  try `http://localhost:8080/api/auth/...` once you have an endpoint to hit.

Repeat the same recipe (own module, own `config-repo/<name>.yml`, own gateway
route) for `customer-service`, `employee-service`, `account-service`, and
`loan-service`.

## Adding Kafka later

Not needed yet — wait until you build the Notification Service and the first
deposit/withdrawal event flow. When you get there, the quickest way to get a
broker running locally is a small `docker-compose.yml` with `confluentinc/cp-kafka`
(or `bitnami/kafka` for a Zookeeper-less KRaft setup). Add
`spring-cloud-starter-stream-kafka` or plain `spring-kafka` to whichever service
needs to publish/consume, and give it its own entry in `config-repo/`.

## A note on `ddl-auto: update`

It's convenient for this learning phase but don't carry that habit into a real
project — switch to Flyway/Liquibase migrations once the schemas stabilize.
