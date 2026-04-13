# Security Configuration

## Architecture

JWT-based stateless authentication with Spring Security.

### Authentication Flow

1. Client sends credentials → `POST /api/auth/login`
2. Server validates credentials
3. Server generates JWT token
4. Client stores token
5. Client includes token in `Authorization: Bearer <token>` header
6. Server validates token and grants access

## Components

### 1. SecurityConfig.java

Main security configuration.

**Key Settings**:
- CSRF disabled (safe for stateless JWT)
- Public endpoints: `/api/auth/**`, `/actuator/health`, `/h2-console/**`
- All other endpoints require authentication
- Stateless session management
- JWT filter runs before standard auth

**Default User** (in-memory):
- Username: `user`
- Password: `password`
- **Change for production**: Replace with database-backed `UserDetailsService`

### 2. JwtUtil.java

Handles JWT token operations.

**Configuration** (from `application.yaml`):
```yaml
jwt:
  secret: ${JWT_SECRET:default-secret}
  expiration: ${JWT_EXPIRATION:86400000} # 24 hours
```

**Methods**:
- `generateToken(username)` - Creates signed JWT
- `validateToken(token, userDetails)` - Verifies signature and expiration
- `extractUsername(token)` - Parses username from token

### 3. JwtAuthenticationFilter.java

Intercepts HTTP requests to validate JWT tokens.

**Process**:
1. Extract `Authorization` header
2. Parse Bearer token
3. Extract username from token
4. Load user details
5. Validate token
6. Set authentication in SecurityContext

### 4. AuthController.java

Handles login requests.

**Endpoint**: `POST /api/auth/login`

Request:
```json
{
  "username": "user",
  "password": "password"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Security Best Practices

### 1. JWT Secret Management

**Generate secure secret** (256-bit minimum):
```bash
openssl rand -base64 32
```

**Never**:
- Commit secrets to version control
- Use default secrets in production
- Share secrets across environments

**Always**:
- Use environment variables: `JWT_SECRET=your-secret`
- Store in secrets manager (AWS Secrets Manager, HashiCorp Vault)
- Rotate regularly

### 2. Token Expiration

**Development**: 24 hours (current default)

**Production**: 15-60 minutes for access tokens

Consider implementing refresh tokens for better security:
```java
String accessToken = jwtUtil.generateToken(username, 900000); // 15 min
String refreshToken = jwtUtil.generateRefreshToken(username, 604800000); // 7 days
```

### 3. HTTPS Only

**Production requirement**: Always use HTTPS to prevent token interception.

### 4. Input Validation

DTOs now include validation:
```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50)
private String username;
```

Global exception handler returns consistent error responses.

### 5. Rate Limiting

Implement to prevent brute-force attacks on login endpoint.

### 6. Password Encoding

BCrypt is configured (10 rounds). For higher security:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Increase strength
}
```

## Role-Based Access Control (RBAC)

Add to controllers:
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public ResponseEntity<?> getAllUsers() { ... }
```

Enable in SecurityConfig (already enabled):
```java
@EnableMethodSecurity(prePostEnabled = true)
```

## Database-Backed Authentication

Replace in-memory user with JPA:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRoles().toArray(new String[0]))
            .build();
    }
}
```

## Common Issues

**Circular dependency error**: Fixed by injecting filter as method parameter in SecurityConfig

**Token not found**: Ensure header format is `Authorization: Bearer <token>`

**Token expired**: Request new token or implement refresh token flow

**Invalid signature**: Verify `JWT_SECRET` matches across environments

## Resources

- [Spring Security Docs](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [OWASP JWT Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
