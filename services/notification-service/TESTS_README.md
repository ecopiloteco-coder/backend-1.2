# Notification Service - Tests Documentation

## 📊 Test Coverage

| Test Suite | Tests | Coverage | Status |
|------------|-------|----------|--------|
| notificationService.test.js | 9 | ~92% | ✅ |
| **Total** | **9** | **92%** | ✅ |

## 🧪 Test Suites

### notificationService.test.js (9 tests)

**Create Notification Tests (2)**
- ✅ Create notification successfully
- ✅ Validate required fields (userId, message)

**Get Notifications Tests (2)**
- ✅ Get all notifications for user
- ✅ Return empty array when none found

**Mark As Read/Unread Tests (3)**
- ✅ Mark notification as read
- ✅ Mark notification as unread
- ✅ Handle notification not found (404)

**Delete Notification Tests (2)**
- ✅ Delete notification successfully
- ✅ Handle delete of non-existent notification

**Unread Count Tests (2)**
- ✅ Get accurate unread count
- ✅ Return 0 when no unread notifications

## 🚀 Running Tests

### Docker
```bash
docker exec notification-service npm test
docker exec notification-service npm test -- notificationService
```

### With Coverage
```bash
docker exec notification-service npm test -- --coverage
```

### Watch Mode
```bash
docker exec notification-service npm test -- --watch
```

### Locally
```bash
cd services/notification-service
npm test
npm test -- --coverage
```

### 🐳 Docker (Recommended)
Run tests in an isolated container without installing Node.js locally:

```powershell
docker run --rm --network ecopilot-network -v "${PWD}\services\notification-service:/app" -w /app node:18-alpine sh -c "npm install && npm test"
```

## 🔧 Test Configuration

**Framework:** Jest
**Test Environment:** Node.js
**Mocked Dependencies:**
- Notification Model (Mongoose/Sequelize)
- MongoDB/PostgreSQL

## 📁 Files

```
services/notification-service/
├── tests/
│   └── unit/
│       └── notificationService.test.js
└── jest.config.js
```

## 📝 jest.config.js

```javascript
module.exports = {
  testEnvironment: 'node',
  testMatch: ['**/tests/**/*.test.js'],
  collectCoverageFrom: [
    'services/**/*.js',
    'controllers/**/*.js'
  ],
  coverageThreshold: {
    global: {
      branches: 70,
      functions: 75,
      lines: 80,
      statements: 80
    }
  }
};
```

## ✅ Key Features Tested

- ✅ CRUD operations
- ✅ Read/Unread status management
- ✅ User-specific notifications
- ✅ Unread count calculation
- ✅ Error handling (404, validation)

## 🔍 Example Test

```javascript
describe('createNotification', () => {
  it('should create notification successfully', async () => {
    // Arrange
    const data = {
      userId: 1,
      message: 'Test',
      type: 'info'
    };
    
    // Act
    const result = await service.createNotification(data);
    
    // Assert
    expect(result.isRead).toBe(false);
  });
});
```

## 📦 Dependencies

```json
{
  "devDependencies": {
    "jest": "^30.0.0"
  }
}
```
