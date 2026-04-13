# Logging Configuration

## Overview

This template uses **Logback** (default in Spring Boot) with SLF4J as the logging facade.

**Configuration Files:**
- `logback-spring.xml` - Main logging configuration with profile support
- `application.yaml` - Basic logging settings

## Understanding Logging

### SLF4J vs Logback

```
Your Code → SLF4J API (interface) → Logback (implementation)
```

- **SLF4J**: Logging facade/API that your code uses
- **Logback**: Actual logging implementation that writes logs

### Usage in Code

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClass {
    public void myMethod() {
        log.debug("Debug message");
        log.info("Info message");
        log.warn("Warning message");
        log.error("Error message", exception);
    }
}
```

Or without Lombok:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
    private static final Logger log = LoggerFactory.getLogger(MyClass.class);
}
```

## Log Levels

From most to least verbose:

| Level | Purpose | Example |
|-------|---------|---------|
| TRACE | Very detailed info | SQL parameter values |
| DEBUG | Debugging info | Method entry/exit, variable values |
| INFO | General info | Application startup, config loaded |
| WARN | Warnings | Deprecated API usage, recoverable errors |
| ERROR | Errors | Exceptions, failures |

## Profile-Based Configuration

### Development Profile

**Activate**: `--spring.profiles.active=dev` or no profile

```xml
<springProfile name="dev">
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    <logger name="com.pmdspringbootrestapitemplate" level="DEBUG"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
</springProfile>
```

**Features**:
- Console output only
- DEBUG level for your application code
- Shows SQL queries and Spring Web details

### Production Profile

**Activate**: `--spring.profiles.active=prod`

```xml
<springProfile name="prod">
    <root level="WARN">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
    <logger name="com.pmdspringbootrestapitemplate" level="INFO"/>
</springProfile>
```

**Features**:
- Both console and file output
- WARN level for third-party libraries
- INFO level for application code
- File rotation enabled

## Log File Configuration

### Location
```
logs/
├── application.log          # Current log file
├── application-2024-01-15.0.log
├── application-2024-01-14.0.log
└── ...
```

### Rotation Policy

- **Size limit**: 10MB per file
- **Daily rollover**: New file each day
- **History**: 30 days
- **Total size cap**: 1GB

When limits are reached, oldest logs are deleted automatically.

## Customizing Logging

### Change Log Level

**application.yaml**:
```yaml
logging:
  level:
    root: INFO
    com.yourpackage: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
```

### Change Log Pattern

```yaml
logging:
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

**Pattern variables**:
- `%d{pattern}` - Date/time
- `%thread` - Thread name
- `%-5level` - Log level (padded to 5 chars)
- `%logger{36}` - Logger name (max 36 chars)
- `%msg` - Log message
- `%n` - New line

### Change File Location

**application.yaml**:
```yaml
logging:
  file:
    name: /var/log/myapp/application.log
```

### Add Custom Appenders

**logback-spring.xml**:
```xml
<!-- Email appender for errors -->
<appender name="EMAIL" class="ch.qos.logback.classic.net.SMTPAppender">
    <smtpHost>smtp.gmail.com</smtpHost>
    <to>admin@example.com</to>
    <from>app@example.com</from>
    <subject>Application Error: %logger{20}</subject>
    <layout class="ch.qos.logback.classic.PatternLayout">
        <pattern>%date %-5level %logger - %msg%n</pattern>
    </layout>
</appender>
```

## JSON Logging (for ELK Stack)

### Add Dependency

**pom.xml**:
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### Configure JSON Output

**logback-spring.xml**:
```xml
<appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.json</file>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/application-%d{yyyy-MM-dd}.json</fileNamePattern>
    </rollingPolicy>
</appender>
```

## Logging Best Practices

### 1. Use Appropriate Levels

```java
// ❌ Bad
log.info("User ID: " + userId); // Too verbose for production

// ✅ Good
log.debug("Processing user ID: {}", userId);
```

### 2. Use Parameterized Messages

```java
// ❌ Bad - String concatenation happens even if debug is disabled
log.debug("User: " + user.getName() + ", Age: " + user.getAge());

// ✅ Good - Parameters only evaluated if debug is enabled
log.debug("User: {}, Age: {}", user.getName(), user.getAge());
```

### 3. Don't Log Sensitive Data

```java
// ❌ Bad
log.info("User logged in: {}, password: {}", username, password);

// ✅ Good
log.info("User logged in: {}", username);
```

### 4. Log Exceptions Properly

```java
// ❌ Bad
log.error("Error occurred: " + e.getMessage());

// ✅ Good
log.error("Error processing user request", e);
```

### 5. Use MDC for Request Tracking

```java
import org.slf4j.MDC;

// Add request ID to all logs in this thread
MDC.put("requestId", UUID.randomUUID().toString());
log.info("Processing request"); // Logs will include requestId
MDC.clear(); // Clean up after request
```

## Correlation ID (Request Tracking)

This template includes automatic correlation ID tracking for all HTTP requests.

### What is a Correlation ID?

A unique identifier (UUID) that tracks a request across your entire application stack. It helps you:
- Trace a single request through logs
- Debug distributed systems
- Monitor request flow across microservices

### How It Works

**CorrelationIdFilter** automatically:
1. Checks incoming request for `X-Correlation-Id` header
2. Generates new UUID if header is missing
3. Stores ID in MDC (Mapped Diagnostic Context)
4. Adds ID to response header
5. Includes ID in all log statements for that request

### Usage

**Client sends correlation ID**:
```bash
curl -H "X-Correlation-Id: my-custom-id-123" http://localhost:8080/api/endpoint
```

**Server generates if missing**:
```bash
curl http://localhost:8080/api/endpoint
# Server generates UUID like: 550e8400-e29b-41d4-a716-446655440000
```

### Log Output Example

```
2024-01-15 10:30:15.123 [http-nio-8080-exec-1] [550e8400-e29b-41d4-a716-446655440000] INFO  c.p.controller.AuthController - User login attempt
2024-01-15 10:30:15.145 [http-nio-8080-exec-1] [550e8400-e29b-41d4-a716-446655440000] DEBUG c.p.security.JwtUtil - Generating JWT token
2024-01-15 10:30:15.167 [http-nio-8080-exec-1] [550e8400-e29b-41d4-a716-446655440000] INFO  c.p.controller.AuthController - Login successful
```

All logs for the same request share the same correlation ID, making it easy to trace the entire request flow.

### Accessing Correlation ID in Code

```java
import org.slf4j.MDC;

String correlationId = MDC.get("correlationId");
// Use in business logic, error reporting, etc.
```

### Microservices Integration

When calling other services, pass the correlation ID:

```java
@Service
public class ExternalServiceClient {

    public void callExternalService() {
        String correlationId = MDC.get("correlationId");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", correlationId);

        // Make HTTP call with headers
        restTemplate.exchange(url, method, new HttpEntity<>(headers), responseType);
    }
}
```

### Customization

**Change header name** in `CorrelationIdFilter.java`:
```java
private static final String CORRELATION_ID_HEADER = "X-Request-Id"; // or your custom header
```

**Change MDC key** in `CorrelationIdFilter.java`:
```java
private static final String CORRELATION_ID_MDC_KEY = "requestId";
```

Then update log pattern to match:
```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{requestId}] %-5level %logger - %msg%n</pattern>
```

## Production Recommendations

### 1. Externalize Log Files
Store logs outside the application directory:
```yaml
logging:
  file:
    name: /var/log/myapp/application.log
```

### 2. Use Log Aggregation
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **Datadog**
- **CloudWatch** (AWS)

### 3. Set Appropriate Levels
- Root: WARN
- Your application: INFO
- Third-party libraries: ERROR

### 4. Enable Async Logging
For high-throughput applications:

**logback-spring.xml**:
```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="FILE"/>
    <queueSize>512</queueSize>
</appender>
```

## Troubleshooting

### Logs Not Appearing

1. Check log level - may be set too high
2. Verify appender configuration
3. Check file permissions for log directory

### Too Many Logs

1. Increase log level (INFO → WARN)
2. Disable specific loggers:
   ```yaml
   logging:
     level:
       org.springframework.web: ERROR
   ```

### Log Files Growing Too Large

1. Adjust rotation settings in `logback-spring.xml`
2. Decrease `maxHistory` or `totalSizeCap`
3. Compress rotated files:
   ```xml
   <fileNamePattern>logs/application-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
   ```

## Environment Variables

Override logging settings via environment:

```bash
# Set log level
LOGGING_LEVEL_ROOT=WARN

# Change log file location
LOGGING_FILE_NAME=/custom/path/app.log

# Enable debug for specific package
LOGGING_LEVEL_COM_YOURPACKAGE=DEBUG
```

## Resources

- [Logback Documentation](https://logback.qos.ch/manual/)
- [Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [SLF4J Manual](https://www.slf4j.org/manual.html)
