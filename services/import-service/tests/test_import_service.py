"""
Unit tests for Import Service Excel Parser

Tests cover:
- Excel file parsing (.xlsx and .xls)
- Sheet names extraction
- Data validation
- Error handling
"""

import pytest
from fastapi.testclient import TestClient
from main import app
import io
import pandas as pd

client = TestClient(app)


class TestExcelParsing:
    """Tests for Excel file parsing functionality"""

    def test_should_parse_xlsx_file_successfully(self):
        """Should successfully parse .xlsx file and return sheet names"""
        # Arrange
        excel_data = self._create_sample_xlsx()
        files = {"file": ("test.xlsx", excel_data, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}

        # Act
        response = client.post("/api/projets/preview-dpgf", files=files)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert "sheets" in data
        assert len(data["sheets"]) > 0

    def test_should_parse_xls_file_with_xlrd(self):
        """Should parse old .xls format using xlrd library"""
        # Arrange
        excel_data = self._create_sample_xls()
        files = {"file": ("test.xls", excel_data, "application/vnd.ms-excel")}

        # Act
        response = client.post("/api/projets/preview-dpgf", files=files)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert "sheets" in data

    def test_should_reject_invalid_file_format(self):
        """Should reject non-Excel files"""
        # Arrange
        invalid_file = io.BytesIO(b"This is not an Excel file")
        files = {"file": ("test.txt", invalid_file, "text/plain")}

        # Act
        response = client.post("/api/projets/preview-dpgf", files=files)

        # Assert
        assert response.status_code == 400

    def test_should_extract_sheet_names_correctly(self):
        """Should correctly extract all sheet names from Excel file"""
        # Arrange
        excel_data = self._create_multi_sheet_xlsx()
        files = {"file": ("multi.xlsx", excel_data, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}

        # Act
        response = client.post("/api/projets/preview-dpgf", files=files)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert "Sheet1" in data["sheets"]
        assert "Sheet2" in data["sheets"]

    def test_should_preview_sheet_rows(self):
        """Should return preview rows for specified sheet"""
        # Arrange
        excel_data = self._create_sample_xlsx()
        files = {"file": ("test.xlsx", excel_data, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}

        # Act
        response = client.post("/api/projets/preview-dpgf-sheet", data={"sheetName": "Sheet1"}, files=files)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert "rows" in data
        assert len(data["rows"]) > 0

    def test_should_handle_empty_excel_file(self):
        """Should handle Excel file with no data"""
        # Arrange
        excel_data = self._create_empty_xlsx()
        files = {"file": ("empty.xlsx", excel_data, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}

        # Act
        response = client.post("/api/projets/preview-dpgf", files=files)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert "sheets" in data

    def test_should_validate_dpgf_structure(self):
        """Should validate DPGF file structure (columns, headers)"""
        # Arrange
        excel_data = self._create_dpgf_xlsx()
        files = {"file": ("dpgf.xlsx", excel_data, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")}

        # Act
        response = client.post("/api/projets/parse-dpgf", files=files)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert "data" in data
        assert len(data["data"]) > 0
        first_row = data["data"][0]
        assert "Niveau_1" in first_row or "Article" in first_row

    # Helper methods
    def _create_sample_xlsx(self):
        """Create a sample .xlsx file for testing"""
        df = pd.DataFrame({
            "Article": ["Béton C25/30", "Acier HA"],
            "Unité": ["m³", "kg"],
            "Prix": [150.0, 2.5]
        })
        buffer = io.BytesIO()
        df.to_excel(buffer, sheet_name="Sheet1", index=False, engine='openpyxl')
        buffer.seek(0)
        return buffer

    def _create_sample_xls(self):
        """Create a sample .xls file for testing"""
        import xlwt
        workbook = xlwt.Workbook()
        sheet = workbook.add_sheet("Sheet1")
        
        # Headers
        sheet.write(0, 0, "Article")
        sheet.write(0, 1, "Prix")
        
        # Row 1
        sheet.write(1, 0, "Béton C25/30")
        sheet.write(1, 1, 150.0)
        
        buffer = io.BytesIO()
        workbook.save(buffer)
        buffer.seek(0)
        return buffer

    def _create_multi_sheet_xlsx(self):
        """Create Excel file with multiple sheets"""
        buffer = io.BytesIO()
        with pd.ExcelWriter(buffer, engine='openpyxl') as writer:
            pd.DataFrame({"Col1": [1, 2, 3]}).to_excel(writer, sheet_name="Sheet1", index=False)
            pd.DataFrame({"Col2": [4, 5, 6]}).to_excel(writer, sheet_name="Sheet2", index=False)
        buffer.seek(0)
        return buffer

    def _create_empty_xlsx(self):
        """Create empty Excel file"""
        df = pd.DataFrame()
        buffer = io.BytesIO()
        df.to_excel(buffer, index=False, engine='openpyxl')
        buffer.seek(0)
        return buffer

    def _create_dpgf_xlsx(self):
        """Create DPGF-formatted Excel file"""
        df = pd.DataFrame({
            "Niveau_1": ["Gros Oeuvre"],
            "Niveau_2": ["Béton"],
            "Niveau_7": ["Béton C25/30"],
            "Unité": ["m³"],
            "Quantité": [100],
            "Prix Unitaire": [150.0]
        })
        buffer = io.BytesIO()
        df.to_excel(buffer, sheet_name="DPGF", index=False, engine='openpyxl')
        buffer.seek(0)
        return buffer


class TestImportServiceEndpoints:
    """Tests for import service HTTP endpoints"""

    def test_health_check(self):
        """Should return healthy status"""
        response = client.get("/health")
        assert response.status_code == 200

    def test_missing_file_parameter(self):
        """Should return error when file parameter is missing"""
        response = client.post("/api/projets/preview-dpgf")
        assert response.status_code == 422
