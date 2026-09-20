# Bank Microservices Starter

Infrastructure layer — **Discovery Server (Eureka) → Config Server → API Gateway**
— plus two domain services: **auth-service** and **customer-service**.

Versions used: **Spring Boot 4.1.1**, **Spring Cloud 2025.1.2 (Oakwood)**, **Java 25 (min. 17)**.
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
├── api-gateway/                <- single entry point for clients, port 8080
├── auth-service/                <- registration, login, JWT issuing, port 8081
├── customer-service/            <- customer profile data, port 8082
└── db-scripts/                  <- one CREATE DATABASE script per database
```

## Run order (this matters)

1. **discovery-server** — `mvn -pl discovery-server spring-boot:run`. Check the
   dashboard at http://localhost:8761 — it should be empty at first.
2. **config-server** — `mvn -pl config-server spring-boot:run`. Sanity check:
   http://localhost:8888/customer-service/default should return the contents of
   `customer-service.yml` as JSON.
3. **api-gateway** — `mvn -pl api-gateway spring-boot:run`. It will show up in
   the Eureka dashboard within ~30 seconds of starting.
4. **auth-service** — `mvn -pl auth-service spring-boot:run`. Requires a running
   Postgres instance first — see "Running auth-service" below.
5. **customer-service** — `mvn -pl customer-service spring-boot:run`. Requires
   `customer_db` to exist first, and requires a JWT from auth-service to call
   any of its endpoints — see "Running customer-service" below.

(No Maven wrapper is bundled — use your own installed `mvn`, either from inside
a module folder with `mvn spring-boot:run`, or from the root with `mvn -pl
<module-name> spring-boot:run`.)

## Running auth-service

**1. Create the database** (matches `config-repo/auth-service.yml`):
```sql
CREATE DATABASE auth_db;
```
Update the `username`/`password` in `config-repo/auth-service.yml` if your local
Postgres doesn't use `postgres`/`postgres`.

**2. Start it** (after discovery-server and config-server are up):
```bash
mvn -pl auth-service spring-boot:run
```
`ddl-auto: update` means Hibernate creates the `users` and `user_roles` tables
for you on first startup.

**3. Try the endpoints directly** (port 8081):
```bash
# Register - creates a ROLE_CUSTOMER user and returns a JWT
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"jane","email":"jane@example.com","password":"password123"}'

# Login - returns a fresh JWT
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"jane","password":"password123"}'

# Call a protected endpoint with the token from either response above
curl http://localhost:8081/api/auth/me \
  -H "Authorization: Bearer <paste the token here>"
```

**4. Or go through the gateway instead** (port 8080) — same routes, prefixed
by nothing extra since the gateway's `Path=/api/auth/**` rule forwards as-is:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"jane","password":"password123"}'
```

## Running customer-service

customer-service never issues JWTs itself — it validates the ones auth-service
issued, using the same shared secret. Every endpoint requires a token; there's
no public "register" here.

**1. Create the database:**
```bash
sudo -u postgres psql < db-scripts/create_customer_db.sql
```

**2. Start it** (after discovery-server, config-server, and api-gateway are up):
```bash
mvn -pl customer-service spring-boot:run
```

**3. Get a token from auth-service first**, then use it here:
```bash
# Grab a token (reuse a user you already registered, or register a new one)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"jane","password":"password123"}'

# Create your profile - paste the token from above
curl -X POST http://localhost:8082/api/customers/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <paste the token here>" \
  -d '{"firstName":"Jane","lastName":"Doe","phoneNumber":"+254700000000","address":"123 Example Street, Nairobi","dateOfBirth":"1995-06-15"}'

# Fetch it back
curl http://localhost:8082/api/customers/me \
  -H "Authorization: Bearer <paste the token here>"
```

Or import `customer-service-postman-collection.json` into Postman instead of
building these by hand — see the Word documentation for the full walkthrough.

## Wiring the next service into this

When you create `employee-service` (or any future module), it needs:

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
config server lives; everything else (port, datasource, secrets) gets pulled
from `config-repo/employee-service.yml`:
```yaml
spring:
  application:
    name: employee-service
  config:
    import: optional:configserver:http://localhost:8888
```

That's it. Start it after the plumbing apps (and auth-service, since employee
accounts will need JWTs too) are up, and:
- It registers itself in Eureka as `EMPLOYEE-SERVICE`.
- It pulls port/datasource settings from the config server.
- Add its route to `api-gateway`'s `application.yml` (uncomment the stubbed
  `id: employee-service` block, which already has `Path=/api/employees/**`)
  so `http://localhost:8080/api/employees/...` forwards to it automatically.

Repeat the same recipe (own module, own `config-repo/<name>.yml`, own gateway
route) for `account-service` and `loan-service`.

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
