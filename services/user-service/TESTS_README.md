# User Service - Tests Documentation

## 📊 Test Coverage

| Test Suite | Tests | Coverage | Status |
|------------|-------|----------|--------|
| AuthServiceTest | 16 | 100% | ✅ |
| UserServiceTest | 17 | 100% | ✅ |
| **Total** | **33** | **100%** | ✅ |

## 🧪 Test Suites

### AuthServiceTest.java (16 tests)

**Login Tests (3)**
- ✅ Login with JWT token generation
- ✅ Handle invalid credentials (401)
- ✅ UserDTO mapping validation

**Signup Tests (6)**
- ✅ Create user in Keycloak + local DB
- ✅ Handle duplicate email (409)
- ✅ Set admin flag for ADMIN/SUPER_ADMIN roles
- ✅ Handle Keycloak conflict recovery
- ✅ Handle Keycloak failures (500)
- ✅ Publish Kafka user_created event

**DTO Mapping (2)**
- ✅ Map User → UserDTO correctly
- ✅ Don't expose sensitive data (Keycloak ID, password)

### UserServiceTest.java (17 tests)

**CRUD Operations (13)**
- ✅ Get all users with mapping
- ✅ Get user by ID with 404 handling
- ✅ Update user with Kafka event
- ✅ Delete user with Kafka event
- ✅ Handle null fields gracefully

**Kafka Events (2)**
- ✅ Publish user_updated event
- ✅ Publish user_deleted event
- ✅ Handle Kafka unavailability

**Edge Cases (2)**
- ✅ Empty user list
- ✅ Null field handling

## 🚀 Running Tests

### Docker (Recommended)
```bash
docker exec user-service mvn test
```

### Specific Test Class
```bash
docker exec user-service mvn test -Dtest=AuthServiceTest
docker exec user-service mvn test -Dtest=UserServiceTest
```

### With Coverage
```bash
docker exec user-service mvn test jacoco:report
```

### Using PowerShell Script
```powershell
.\run-tests.ps1 -Service user-service
.\run-tests.ps1 -Service user-service -TestClass AuthServiceTest
```

### 🐳 Docker (Recommended)
Run tests in an isolated container without installing Maven/Java locally:

```powershell
docker run --rm --network ecopilot-network -v "${PWD}\services\user-service:/app" -w /app maven:3.9-eclipse-temurin-17 mvn test
```

## 🔧 Test Configuration

**Test Database:** H2 in-memory (PostgreSQL mode)
**Framework:** JUnit 5 + Mockito + AssertJ
**Mocked Dependencies:**
- Keycloak Admin Client
- UserRepository
- JwtUtil
- UserEventProducer

## 📁 Files

```
src/test/java/com/ecopilot/user/service/
├── AuthServiceTest.java
└── UserServiceTest.java

src/test/resources/
└── application-test.yml
```

## ✅ Clean Code Practices

- ✅ AAA Pattern (Arrange-Act-Assert)
- ✅ @DisplayName for readability
- ✅ @Nested for organization
- ✅ AssertJ fluent assertions
- ✅ Complete mocking isolation

## 📚 Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```
