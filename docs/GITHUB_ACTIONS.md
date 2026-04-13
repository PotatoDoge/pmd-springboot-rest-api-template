# GitHub Actions CI Pipeline

## Overview

Automated CI pipeline that runs on every push and pull request to `main` and `develop` branches.

**Location**: `.github/workflows/ci.yml`

## What It Does

1. **Checkout Code** - Clones the repository
2. **Set up JDK 21** - Installs Java 21 with Maven caching
3. **Compile** - Compiles source code (`./mvnw compile`)
4. **Run Tests** - Executes all tests (`./mvnw test`)
5. **Build** - Creates JAR package (`./mvnw package -DskipTests`)

## Extending the Pipeline

### Add Docker Build

```yaml
- name: Build Docker image
  run: docker build -t your-app:${{ github.sha }} .
```

### Add Docker Push

```yaml
- name: Push to Docker Hub
  if: github.ref == 'refs/heads/main'
  run: |
    echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
    docker push your-app:${{ github.sha }}
```

**Required Secrets**: `DOCKER_USERNAME`, `DOCKER_PASSWORD`

### Add Code Coverage

```yaml
- name: Generate coverage report
  run: ./mvnw jacoco:report

- name: Upload to Codecov
  uses: codecov/codecov-action@v3
```

## GitHub Secrets

Add secrets in: **Settings → Secrets → New repository secret**

Common secrets:
- `DOCKER_USERNAME` / `DOCKER_PASSWORD`
- `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY`
- Database credentials for integration tests

## Status Badge

Add to README:
```markdown
![CI](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/ci.yml/badge.svg)
```

## Troubleshooting

**Build fails on dependency download**
- Check Maven Central accessibility
- Verify `pom.xml` syntax

**Tests pass locally but fail in CI**
- Check for environment-specific configs
- Verify test database availability
