# Import Service - Tests Documentation

## 📊 Test Coverage

| Test Suite | Tests | Coverage | Status |
|------------|-------|----------|--------|
| test_import_service | 10 | ~90% | ✅ |
| **Total** | **10** | **90%** | ✅ |

## 🧪 Test Suites

### test_import_service.py (10 tests)

**Excel Parsing Tests (7)**
- ✅ Parse .xlsx files successfully
- ✅ Parse .xls files with xlrd
- ✅ Reject invalid file formats (422)
- ✅ Extract sheet names correctly
- ✅ Preview sheet rows
- ✅ Handle empty Excel files
- ✅ Validate DPGF structure

**Endpoint Tests (2)**
- ✅ Health check endpoint
- ✅ Missing file parameter error (422)

## 🚀 Running Tests

### Docker
```bash
docker exec import-service pytest
docker exec import-service pytest -v
docker exec import-service pytest tests/test_import_service.py
```

### With Coverage
```bash
docker exec import-service pytest --cov=. --cov-report=html
```

### Locally (if pytest installed)
```bash
cd services/import-service
pytest
pytest -v --tb=short
```

### 🐳 Docker (Recommended)
Run tests in an isolated container without installing Python locally:

```powershell
docker run --rm --network ecopilot-network -v "${PWD}\services\import-service:/app" -w /app python:3.9 sh -c "pip install -r requirements.txt && pytest"
```

## 🔧 Test Configuration

**Framework:** pytest + FastAPI TestClient
**Test Files:** Dynamically created in-memory Excel files
**Dependencies:**
- pytest
- httpx
- pandas
- openpyxl (for .xlsx)
- xlrd (for .xls)

## 📁 Files

```
services/import-service/
├── tests/
│   └── test_import_service.py
├── conftest.py          (pytest fixtures)
└── pytest.ini           (pytest config)
```

## 📝 pytest.ini Configuration

```ini
[pytest]
testpaths = tests
python_files = test_*.py
asyncio_mode = auto
addopts = -v --tb=short
```

## ✅ Key Features Tested

- ✅ .xlsx file parsing (openpyxl)
- ✅ .xls file parsing (xlrd)
- ✅ Sheet names extraction
- ✅ Row preview functionality
- ✅ DPGF format validation
- ✅ Error handling (invalid files)

## 🧰 Helper Methods

```python
_create_sample_xlsx()      # Create test .xlsx file
_create_sample_xls()       # Create test .xls file
_create_multi_sheet_xlsx() # Multiple sheets
_create_empty_xlsx()       # Empty file
_create_dpgf_xlsx()        # DPGF-formatted file
```

## 📊 Test Fixtures

```python
@pytest.fixture
def client():
    """FastAPI TestClient"""

@pytest.fixture
def sample_excel_file():
    """In-memory Excel file"""
```
