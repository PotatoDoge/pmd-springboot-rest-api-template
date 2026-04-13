# TL;DR - Quick Start Guide

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
# Clone
git clone <your-repo>
cd pmd-springboot-rest-api-template

# (Optional) Copy and configure environment variables
cp .env.example .env
# Edit .env with your values

# Run
./mvnw spring-boot:run

# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'

# Use token
curl http://localhost:8080/api/your-endpoint \
  -H "Authorization: Bearer <your-token>"
```

**Default credentials**: `user` / `password`

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

**Copy .env.example to .env**:
```bash
cp .env.example .env
```

**Edit .env**:
```bash
# Update these values
JWT_SECRET=<generate-secure-256-bit-secret>
JWT_EXPIRATION=900000  # 15 minutes for production
DATABASE_URL=jdbc:postgresql://localhost:5432/yourdb
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-password
```

Generate JWT secret:
```bash
openssl rand -base64 32
```

### 6. Replace In-Memory Users

**`SecurityConfig.java`** - Replace with database-backed `UserDetailsService`:
```java
@Bean
public UserDetailsService userDetailsService() {
    // TODO: Replace with your UserRepository implementation
    return username -> userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
}
```

### 7. Switch Database (Optional)

See [docs/DATABASE.md](docs/DATABASE.md)

**For PostgreSQL**, add to `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

Update `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/yourdb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

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

## Features Included

✅ JWT authentication with token validation
✅ Global exception handling with validation
✅ Correlation ID request tracking
✅ Profile-based logging (dev/prod)
✅ H2 database with migration guides
✅ Docker multi-stage build
✅ GitHub Actions CI pipeline
✅ Health check endpoints

## Common Tasks

### Run Locally
```bash
./mvnw spring-boot:run
```

### Run with Docker
```bash
docker-compose up --build
```

### Run Tests
```bash
./mvnw test
```

### Build JAR
```bash
./mvnw package
```

### Access H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### View Logs
```bash
# Console logs appear automatically
# File logs at: logs/application.log
```

### Change Log Level
**`application.yaml`**:
```yaml
logging:
  level:
    root: WARN
    com.yourcompany.yourproject: DEBUG
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | (see application.yaml) | JWT signing key (min 256-bit) |
| `JWT_EXPIRATION` | 86400000 | Token expiration (ms) |
| `DATABASE_URL` | jdbc:h2:mem:testdb | Database connection |
| `SPRING_PROFILES_ACTIVE` | (none) | Active profile (dev/prod) |

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

For detailed information, see:

- **[README.md](README.md)** - Full project documentation
- **[docs/DATABASE.md](docs/DATABASE.md)** - Database configuration
- **[docs/SECURITY.md](docs/SECURITY.md)** - Security & JWT
- **[docs/LOGGING.md](docs/LOGGING.md)** - Logging & correlation IDs
- **[docs/GITHUB_ACTIONS.md](docs/GITHUB_ACTIONS.md)** - CI/CD pipeline

## Need Help?

1. Check the docs folder
2. Review example code in controllers/security
3. Check application logs in `logs/application.log`
4. Verify configuration in `application.yaml`

## Project Structure

```
src/main/java/com/pmdspringbootrestapitemplate/
├── config/              # SecurityConfig, CorrelationIdFilter
├── controller/          # AuthController (add your controllers here)
├── dto/                # AuthRequest, AuthResponse (add your DTOs here)
├── exception/          # GlobalExceptionHandler, custom exceptions
└── security/           # JwtUtil, JwtAuthenticationFilter
```

## Next Steps

1. Rename package to your project name
2. Update pom.xml with your project details
3. Add your first entity/repository/service/controller
4. Replace in-memory users with database
5. Add your business logic
6. Write tests
7. Deploy!

---

**That's it!** You now have a production-ready REST API template. Start building your features! 🚀
