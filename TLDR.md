# TL;DR - Quick Start Guide

> **This guide summarizes all documentation from [docs/](docs/) - see individual docs for detailed instructions.**

## What Is This?

Spring Boot 4.0.5 REST API template with JWT auth, H2 database, Docker, CI/CD, logging, and exception handling.

## Stack

- **Java 21** + **Spring Boot 4.0.5**
- **JWT** authentication (stateless)
- **H2** database (in-memory, switchable to PostgreSQL/MySQL)
- **Logback** logging with correlation IDs
- **Docker** + **Docker Compose**
- **GitHub Actions** CI

## Quick Start

```bash
# Run locally
./mvnw spring-boot:run

# Or with Docker
docker-compose up --build

# Test login (user/password)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```

## Adapt to Your Project

### 1. Update Project Identity

**`pom.xml`**:
```xml
<groupId>com.yourcompany</groupId>
<artifactId>your-project-name</artifactId>
<name>your-project-name</name>
<description>Your project description</description>
```

### 2. Rename Package

Rename: `com.pmdspringbootrestapitemplate` → `com.yourcompany.yourproject`

```bash
# In your IDE, refactor/rename the package
# Or manually move files and update imports
```

### 3. Update Application Name

**`application.yaml`**:
```yaml
spring:
  application:
    name: your-project-name
```

### 4. Update Docker Names

**`docker-compose.yml`**:
```yaml
services:
  app:
    container_name: your-app-name  # Change this
```

### 5. Set Production Secrets

```bash
# Generate JWT secret
openssl rand -base64 32

# Set environment variables
export JWT_SECRET=<your-generated-secret>
export JWT_EXPIRATION=900000  # 15 min for production
```

### 6. Replace In-Memory Users & Switch Database

See [docs/DATABASE.md](docs/DATABASE.md) and [docs/SECURITY.md](docs/SECURITY.md) for details.

## Key Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/auth/login` | POST | No | Login, get JWT token |
| `/actuator/health` | GET | No | Health check |
| `/h2-console` | GET | No | H2 database console (dev only) |

## Key Files to Customize

| File | What to Change |
|------|----------------|
| `pom.xml` | groupId, artifactId, name, description |
| `application.yaml` | spring.application.name, database config |
| `SecurityConfig.java` | Replace in-memory users |
| `docker-compose.yml` | container_name |
| `.github/workflows/ci.yml` | Docker image names (if pushing to registry) |

## Features

✅ JWT authentication | ✅ Global exception handling | ✅ Correlation ID tracking
✅ Profile-based logging | ✅ H2 database | ✅ Docker support | ✅ GitHub Actions CI

## Production Checklist

- [ ] Change `JWT_SECRET` environment variable
- [ ] Reduce `JWT_EXPIRATION` to 15-60 minutes
- [ ] Replace in-memory users with database
- [ ] Switch from H2 to PostgreSQL/MySQL
- [ ] Set `spring.jpa.hibernate.ddl-auto: validate`
- [ ] Disable H2 console (`spring.h2.console.enabled: false`)
- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Configure HTTPS/SSL
- [ ] Review and update CORS settings
- [ ] Add rate limiting to login endpoint
- [ ] Configure log aggregation (ELK/Splunk/CloudWatch)
- [ ] Update default username/password

## Documentation

- **[README.md](README.md)** - Full overview
- **[docs/DATABASE.md](docs/DATABASE.md)** - Database setup (PostgreSQL/MySQL)
- **[docs/SECURITY.md](docs/SECURITY.md)** - JWT, CORS, authentication
- **[docs/LOGGING.md](docs/LOGGING.md)** - Logging configuration
- **[docs/GITHUB_ACTIONS.md](docs/GITHUB_ACTIONS.md)** - CI/CD setup
- **[docs/ENVIRONMENT.md](docs/ENVIRONMENT.md)** - Environment variables

---

**That's it!** You now have a production-ready REST API template. Start building your features! 🚀
