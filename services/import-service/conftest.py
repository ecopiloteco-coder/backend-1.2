"""
Pytest configuration and fixtures for import-service tests
"""

import pytest
from fastapi.testclient import TestClient


@pytest.fixture
def client():
    """Test client fixture"""
    from main import app
    return TestClient(app)


@pytest.fixture
def sample_excel_file():
    """Sample Excel file fixture for testing"""
    import io
    import pandas as pd
    
    df = pd.DataFrame({
        "Article": ["Test Article"],
        "Prix": [100.0]
    })
    
    buffer = io.BytesIO()
    df.to_excel(buffer, index=False, engine='openpyxl')
    buffer.seek(0)
    return buffer
