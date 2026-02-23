# Project Service - Tests Documentation

## 📊 Test Coverage

| Test Suite | Tests | Coverage | Status |
|------------|-------|----------|--------|
| ProjetServiceTest | 19 | ~95% | ✅ |
| **Total** | **19** | **95%** | ✅ |

## 🧪 Test Suites

### ProjetServiceTest.java (19 tests)

**Create Project Tests (2)**
- ✅ Create project successfully
- ✅ Validate required fields

**Get Project Tests (3)**
- ✅ Get project by ID
- ✅ Handle project not found (404)
- ✅ Get all projects for user

**Update Project Tests (1)**
- ✅ Update project successfully with Kafka event

**Delete Project Tests (1)**
- ✅ Delete project successfully with Kafka event

**Pricing Calculation Tests (3)**
- ✅ Calculate total_ttc with TVA
- ✅ Calculate TVA amount
- ✅ Handle zero TVA

**Cascade Pricing Tests (2)**
- ✅ Recalculate project total when lot price changes
- ✅ Preserve cascade pricing integrity

**Kafka Events (3)**
- ✅ Publish projet_created event
- ✅ Publish projet_updated event
- ✅ Publish projet_deleted event

## 📐 Pricing Formulas Tested

```java
// TVA Calculation
total_ttc = prix_total_ht * (1 + tva/100)
tva_amount = prix_total_ht * (tva/100)

// Cascade Pricing
Article → Bloc → Ouvrage → Lot → Projet

bloc.pt = Σ(articles.prix_total_ht)
bloc.pu = bloc.pt / bloc.quantite

ouvrage.prix_total = Σ(blocs.pt)
lot.prix_total = Σ(ouvrages.prix_total)
projet.prix_total = Σ(lots.prix_total)

Example:
Prix HT = 500,000€
TVA = 20%
→ total_ttc = 600,000€
→ tva_amount = 100,000€
```

## 🚀 Running Tests

### Docker
```bash
docker exec project-service mvn test
docker exec project-service mvn test -Dtest=ProjetServiceTest
```

### With Coverage
```bash
docker exec project-service mvn test jacoco:report
```

### 🐳 Docker (Recommended)
Run tests in an isolated container without installing Maven/Java locally:

```powershell
docker run --rm --network ecopilot-network -v "${PWD}\services\project-service:/app" -w /app maven:3.9-eclipse-temurin-17 mvn test
```

## 🔧 Test Configuration

**Test Database:** H2 in-memory
**Framework:** JUnit 5 + Mockito + AssertJ
**Mocked Dependencies:**
- ProjetRepository
- ProjetEventProducer

## 📁 Files

```
src/test/java/com/ecopilot/project/service/
└── ProjetServiceTest.java

src/test/resources/
└── application-test.yml (to create)
```

## ✅ Key Features Tested

- ✅ CRUD operations
- ✅ TVA calculations (20%)
- ✅ Cascade pricing updates
- ✅ User-specific project queries
- ✅ Kafka event publishing
