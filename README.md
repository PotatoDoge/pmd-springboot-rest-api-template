# Spring Boot REST API Template

A production-ready Spring Boot REST API template with JWT authentication, Docker support, and CI/CD pipeline.

> ⚡ **[TLDR.md](TLDR.md)** - Quick start guide to adapt this template to your project
>
> 📚 **For detailed documentation, see the [docs/](docs/) folder**

## Features

- Spring Boot 4.0.5 with Java 21
- JWT authentication with stateless sessions
- H2 in-memory database (easily switchable to PostgreSQL/MySQL)
- Global exception handling with validation
- Logback logging with profile-based configuration
- Docker & Docker Compose configuration
- GitHub Actions CI pipeline
- Spring Security with BCrypt password encoding
- Health check endpoints via Actuator

## Quick Start

```bash
# Run with Docker Compose
docker-compose up --build

# Or run locally
./mvnw spring-boot:run
```

**Default credentials:** username: `user`, password: `password`

## Required Changes for New Projects

### 1. Project Identity (`pom.xml`)
- `<groupId>` - Change from `com` to your organization (e.g., `com.yourcompany`)
- `<artifactId>` - Change from `pmd-springboot-rest-api-template` to your project name
- `<name>` and `<description>` - Update with your project details

### 2. Package Structure
Rename the base package from `com.pmdspringbootrestapitemplate` to match your new groupId and artifactId:
```
src/main/java/com/pmdspringbootrestapitemplate/ → src/main/java/your/package/name/
```

### 3. Application Configuration (`src/main/resources/application.yaml`)
- `spring.application.name` - Update to your application name

### 4. Database Configuration
See [docs/DATABASE.md](docs/DATABASE.md) for switching from H2 to PostgreSQL/MySQL.

### 5. Docker Configuration
- **`Dockerfile`** - Update image/container references if needed
- **`docker-compose.yml`** - Change `container_name` from `pmd-springboot-api` to your app name

### 6. GitHub Actions
See [docs/GITHUB_ACTIONS.md](docs/GITHUB_ACTIONS.md) for detailed CI/CD configuration.

### 7. Security Configuration
See [docs/SECURITY.md](docs/SECURITY.md) for comprehensive security documentation.
- **JWT Secret** - Set `JWT_SECRET` environment variable in production (minimum 256 bits)
- **User Credentials** - Replace in-memory user in `SecurityConfig.java` with database-backed authentication

## Environment Variables

**Setup**: Copy `.env.example` to `.env` and update values:
```bash
cp .env.example .env
```

**Key variables**:

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | (see .env.example) | JWT signing key (256-bit minimum) |
| `JWT_EXPIRATION` | 86400000 | Token expiration in milliseconds (24h) |
| `DATABASE_URL` | jdbc:h2:mem:testdb | Database connection URL |
| `DATABASE_USERNAME` | sa | Database username |
| `DATABASE_PASSWORD` | (empty) | Database password |
| `SPRING_PROFILES_ACTIVE` | (none) | Active profile (dev/prod) |

See `.env.example` for complete list of configurable variables.

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login and receive JWT token
  ```bash
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"user","password":"password"}'
  ```

### Health Check
- `GET /actuator/health` - Application health status

### H2 Console (Development Only)
- `GET /h2-console` - Database console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)

### Protected Endpoints
Include JWT token in Authorization header:
```bash
curl http://localhost:8080/api/your-endpoint \
  -H "Authorization: Bearer <your-token>"
```

## Development

```bash
# Compile
./mvnw compile

# Run tests
./mvnw test

# Build package
./mvnw package

# Run application
./mvnw spring-boot:run
```

## Project Structure

```
.
├── src/main/java/com/pmdspringbootrestapitemplate/
│   ├── config/          # Security and app configuration
│   ├── controller/      # REST controllers
│   ├── dto/            # Data transfer objects
│   └── security/       # JWT utilities and filters
├── docs/               # Detailed documentation
│   ├── DATABASE.md        # Database configuration guide
│   ├── ENVIRONMENT.md     # Environment variables guide
│   ├── GITHUB_ACTIONS.md  # CI/CD pipeline guide
│   ├── LOGGING.md         # Logging configuration guide
│   └── SECURITY.md        # Security configuration guide
├── exception/          # Global exception handling
├── .env.example        # Environment variables template
├── Dockerfile          # Multi-stage Docker build
├── docker-compose.yml  # Container orchestration
└── .github/workflows/  # CI/CD pipeline
```

## Documentation

- **[Database Configuration](docs/DATABASE.md)** - Switch from H2 to PostgreSQL/MySQL
- **[Environment Variables](docs/ENVIRONMENT.md)** - Configuration management and .env usage
- **[GitHub Actions Guide](docs/GITHUB_ACTIONS.md)** - CI/CD pipeline configuration
- **[Logging Configuration](docs/LOGGING.md)** - Logging setup and best practices
- **[Security Guide](docs/SECURITY.md)** - JWT authentication and best practices
