# Environment Variables

## Overview

This template uses environment variables for configuration management, following the [12-Factor App](https://12factor.net/config) methodology.

## Quick Setup

1. **Copy the example file**:
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your values**:
   ```bash
   # Open in your editor
   nano .env
   # or
   vim .env
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

Spring Boot automatically loads `.env` files (when using appropriate libraries or Docker).

## .env vs application.yaml

**application.yaml**: Default configuration (committed to git)
```yaml
jwt:
  secret: ${JWT_SECRET:default-value-for-development}
```

**.env**: Environment-specific overrides (NOT committed to git)
```bash
JWT_SECRET=production-secret-value
```

`.env` values override `application.yaml` defaults.

## Available Variables

### Application Configuration

```bash
# Application profile (dev, prod)
SPRING_PROFILES_ACTIVE=dev

# Server port
SERVER_PORT=8080
```

### Database Configuration

**H2 (Development)**:
```bash
DATABASE_URL=jdbc:h2:mem:testdb
DATABASE_DRIVER=org.h2.Driver
DATABASE_USERNAME=sa
DATABASE_PASSWORD=
```

**PostgreSQL (Production)**:
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/yourdb
DATABASE_DRIVER=org.postgresql.Driver
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-secure-password
```

**MySQL (Production)**:
```bash
DATABASE_URL=jdbc:mysql://localhost:3306/yourdb
DATABASE_DRIVER=com.mysql.cj.jdbc.Driver
DATABASE_USERNAME=root
DATABASE_PASSWORD=your-secure-password
```

### JPA/Hibernate

```bash
# DDL auto mode: none, validate, update, create, create-drop
JPA_DDL_AUTO=update

# Show SQL queries in logs
JPA_SHOW_SQL=true
```

### JWT Configuration

```bash
# JWT signing secret (256-bit minimum)
JWT_SECRET=your-256-bit-secret-key-change-this-in-production-must-be-at-least-256-bits

# Token expiration in milliseconds
# 900000 = 15 minutes
# 3600000 = 1 hour
# 86400000 = 24 hours
JWT_EXPIRATION=86400000
```

**Generate secure secret**:
```bash
openssl rand -base64 32
```

### Logging

```bash
# Root log level: TRACE, DEBUG, INFO, WARN, ERROR
LOGGING_LEVEL_ROOT=INFO

# Application log level
LOGGING_LEVEL_APP=DEBUG
```

### H2 Console

```bash
# Enable/disable H2 console (disable in production!)
H2_CONSOLE_ENABLED=true
```

## Using Environment Variables

### Method 1: .env File (Recommended for Local Development)

1. Create `.env` file (gitignored)
2. Add variables
3. Use with Docker Compose (reads `.env` automatically)

**docker-compose.yml**:
```yaml
services:
  app:
    env_file:
      - .env
```

### Method 2: System Environment Variables

**Linux/Mac**:
```bash
export JWT_SECRET="my-secret"
export DATABASE_URL="jdbc:postgresql://localhost:5432/mydb"
./mvnw spring-boot:run
```

**Windows (CMD)**:
```cmd
set JWT_SECRET=my-secret
set DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
mvnw spring-boot:run
```

**Windows (PowerShell)**:
```powershell
$env:JWT_SECRET="my-secret"
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/mydb"
./mvnw spring-boot:run
```

### Method 3: IDE Configuration

**IntelliJ IDEA**:
1. Run → Edit Configurations
2. Environment Variables → Add
3. Add `JWT_SECRET=value`, `DATABASE_URL=value`, etc.

**VS Code** (launch.json):
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot",
      "env": {
        "JWT_SECRET": "my-secret",
        "DATABASE_URL": "jdbc:postgresql://localhost:5432/mydb"
      }
    }
  ]
}
```

### Method 4: Command Line

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--JWT_SECRET=my-secret --DATABASE_URL=jdbc:postgresql://localhost:5432/mydb"
```

Or:
```bash
java -jar target/app.jar --JWT_SECRET=my-secret --DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
```

## Production Deployment

### Docker

**Dockerfile** (already configured):
```dockerfile
# Environment variables passed at runtime
ENV JWT_SECRET=${JWT_SECRET}
ENV DATABASE_URL=${DATABASE_URL}
```

**Run container**:
```bash
docker run -e JWT_SECRET="prod-secret" -e DATABASE_URL="jdbc:postgresql://db:5432/prod" myapp
```

### Kubernetes

**deployment.yaml**:
```yaml
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: app-secrets
        key: jwt-secret
  - name: DATABASE_URL
    valueFrom:
      configMapKeyRef:
        name: app-config
        key: database-url
```

### Cloud Platforms

**AWS Elastic Beanstalk**:
- Configuration → Software → Environment Properties

**Heroku**:
```bash
heroku config:set JWT_SECRET=my-secret
heroku config:set DATABASE_URL=postgres://...
```

**Azure App Service**:
- Configuration → Application Settings

**Google Cloud Run**:
```bash
gcloud run deploy --set-env-vars JWT_SECRET=my-secret,DATABASE_URL=postgres://...
```

## Security Best Practices

### ✅ DO

- Use `.env` for local development
- Use secrets managers in production (AWS Secrets Manager, HashiCorp Vault, etc.)
- Rotate secrets regularly
- Use different secrets per environment (dev/staging/prod)
- Generate strong random secrets (`openssl rand -base64 32`)

### ❌ DON'T

- Commit `.env` to git (it's gitignored)
- Share `.env` files via email/Slack
- Use production secrets in development
- Hardcode secrets in code
- Use weak/default secrets in production

## Troubleshooting

### Variable Not Being Read

1. **Check spelling**: Environment variables are case-sensitive
2. **Check prefix**: Spring Boot expects `SPRING_`, `SERVER_`, etc.
3. **Check IDE**: Some IDEs need restart to pick up new env vars
4. **Check Docker**: Ensure `env_file` or `-e` flags are set

### Docker Compose Not Reading .env

**Issue**: Variables not loaded

**Solution**:
```yaml
services:
  app:
    env_file:
      - .env  # Add this
```

### Spring Boot Not Finding Variables

**Issue**: `${VAR_NAME}` shows as literal string

**Solution**: Use `${VAR_NAME:default-value}` format in `application.yaml`

## Example Production Setup

### .env.production
```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

DATABASE_URL=jdbc:postgresql://prod-db.example.com:5432/myapp
DATABASE_USERNAME=app_user
DATABASE_PASSWORD=super-secure-password-from-secrets-manager

JWT_SECRET=vK8x2mN9pQ4rT7wZ5aB3cD6eF1gH0iJ2kL4mN7oP9qR8sT5uV3wX6yZ9aB2cD5
JWT_EXPIRATION=900000

JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false

H2_CONSOLE_ENABLED=false

LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_APP=INFO
```

### Load in Production
```bash
# Option 1: Export before running
set -a; source .env.production; set +a
java -jar app.jar

# Option 2: Use Docker
docker run --env-file .env.production myapp

# Option 3: Use secrets manager (recommended)
# AWS Secrets Manager, HashiCorp Vault, etc.
```

## Resources

- [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [12-Factor App: Config](https://12factor.net/config)
- [Docker Environment Variables](https://docs.docker.com/compose/environment-variables/)
