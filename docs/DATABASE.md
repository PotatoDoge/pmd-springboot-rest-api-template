# Database Configuration

## Default Database (H2)

The template uses **H2** in-memory database by default for quick development and testing.

### Configuration
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
```

### Access H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Switching to PostgreSQL

### 1. Add PostgreSQL Dependency

**pom.xml**:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Remove or comment out H2 dependency:
```xml
<!-- <dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency> -->
```

### 2. Update Application Configuration

**application.yaml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database_name
    driver-class-name: org.postgresql.Driver
    username: your_username
    password: your_password

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. Environment Variables (Recommended)

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/mydb}
    username: ${DATABASE_USERNAME:postgres}
    password: ${DATABASE_PASSWORD:password}
```

### 4. Docker Compose Setup

Add PostgreSQL to `docker-compose.yml`:
```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: pmd-postgres
    environment:
      POSTGRES_DB: mydb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    networks:
      - app-network

  app:
    # ... existing app configuration
    depends_on:
      - postgres
    environment:
      - DATABASE_URL=jdbc:postgresql://postgres:5432/mydb
      - DATABASE_USERNAME=postgres
      - DATABASE_PASSWORD=password

volumes:
  postgres-data:
```

## Switching to MySQL

### 1. Add MySQL Dependency

**pom.xml**:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Update Application Configuration

**application.yaml**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database_name
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password

  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. Docker Compose Setup

```yaml
services:
  mysql:
    image: mysql:8
    container_name: pmd-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mydb
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - app-network

volumes:
  mysql-data:
```

## Hibernate DDL Auto Options

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: [option]
```

| Option | Description | Use Case |
|--------|-------------|----------|
| `none` | No schema management | Production (use migrations instead) |
| `validate` | Validate schema, no changes | Production |
| `update` | Update schema if needed | Development |
| `create` | Drop and create schema on startup | Testing |
| `create-drop` | Drop schema on shutdown | Testing |

**Recommendation**: Use `validate` or `none` in production with proper migration tools (Flyway/Liquibase).

## Production Best Practices

1. **Never use `ddl-auto: update` in production**
2. **Use database migration tools**: Flyway or Liquibase
3. **Store credentials in environment variables or secrets manager**
4. **Use connection pooling** (HikariCP is default in Spring Boot)
5. **Disable H2 console in production**
6. **Enable SSL for database connections**

## Connection Pool Configuration

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

## Multiple Database Profiles

Create profile-specific configurations:

**application-dev.yaml**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  h2:
    console:
      enabled: true
```

**application-prod.yaml**:
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  h2:
    console:
      enabled: false
  jpa:
    hibernate:
      ddl-auto: validate
```

Activate profile: `--spring.profiles.active=prod`
