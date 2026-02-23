import io
import json
import math
import os
from typing import List, Optional, Any

import pandas as pd
import math


def _get_engine(file_name: str) -> Optional[str]:
    ext = os.path.splitext(file_name.lower())[1]
    if ext == ".xlsx":
        return "openpyxl"
    if ext == ".xls":
        return "xlrd"
    return None


def get_sheet_names(file_content, file_name: Optional[str] = None) -> List[str]:
    try:
        engine = _get_engine(file_name or "")
        excel_file = pd.ExcelFile(io.BytesIO(file_content), engine=engine)
        return excel_file.sheet_names
    except Exception as e:
        raise Exception(f"Failed to read sheet names: {str(e)}")


def get_sheet_rows(file_content, sheet_name, file_name: Optional[str] = None):
    try:
        engine = _get_engine(file_name or "")
        df = pd.read_excel(io.BytesIO(file_content), sheet_name=sheet_name, header=None, engine=engine)
        rows = df.head(50).fillna("").values.tolist()
        return rows
    except Exception as e:
        raise Exception(f"Failed to read sheet rows: {str(e)}")


def _parse_header_row_param(header_row: Optional[str]) -> Optional[int]:
    if not header_row:
        return None
    parts: List[int] = []
    for part in str(header_row).split("-"):
        part = part.strip()
        if not part:
            continue
        try:
            value = int(part)
        except ValueError:
            continue
        if value > 0:
            parts.append(value)
    if not parts:
        return None
    first = parts[0]
    second = parts[1] if len(parts) > 1 else parts[0]
    return max(first, second)


def _to_int_param(value: Optional[str]) -> Optional[int]:
    if value is None:
        return None
    value = str(value).strip()
    if not value:
        return None
    try:
        parsed = int(value)
    except ValueError:
        return None
    return parsed if parsed > 0 else None


def _safe_float(value: Any) -> Optional[float]:
    if value is None or value == "":
        return None
    try:
        result = float(value)
        # Check for infinity or NaN values which are not JSON compliant
        if math.isinf(result) or math.isnan(result):
            return None
        return result
    except (TypeError, ValueError):
        return None


def parse_dpgf(
    file_content,
    file_name: Optional[str] = None,
    selected_sheets: Optional[str] = None,
    header_row: Optional[str] = None,
    col_designation: Optional[str] = None,
    col_type: Optional[str] = None,
    col_unite: Optional[str] = None,
    col_qte: Optional[str] = None,
    col_pu: Optional[str] = None,
    col_prix_total: Optional[str] = None,
    preview: bool = False,
):
    try:
        engine = _get_engine(file_name or "")

        if not selected_sheets:
            df = pd.read_excel(io.BytesIO(file_content), engine=engine)
            records = df.to_dict(orient="records")
            if preview:
                return records[:5]
            return records

        try:
            sheets_list = json.loads(selected_sheets)
        except json.JSONDecodeError:
            sheets_list = []

        if not isinstance(sheets_list, list):
            sheets_list = []

        header_row_param = _parse_header_row_param(header_row)
        col_designation_param = _to_int_param(col_designation)
        col_type_param = _to_int_param(col_type)
        col_unite_param = _to_int_param(col_unite)
        col_qte_param = _to_int_param(col_qte)
        col_pu_param = _to_int_param(col_pu)
        col_prix_total_param = _to_int_param(col_prix_total)

        excel_file = pd.ExcelFile(io.BytesIO(file_content), engine=engine)
        parsed_data = []

        for sheet_name in sheets_list:
            if sheet_name not in excel_file.sheet_names:
                continue

            df = excel_file.parse(sheet_name=sheet_name, header=None)
            data = df.values.tolist()

            lot_data = {
                "name": sheet_name,
                "ouvrages": [],
            }

            current_ouvrage = None
            current_bloc = None
            start_index = 0
            header_row_index = -1

            if header_row_param is not None and header_row_param > 0:
                header_row_index = header_row_param - 1
                start_index = header_row_index + 1
            else:
                header_keywords = [
                    "designation",
                    "ouvrage",
                    "bloc",
                    "projet article",
                    "unité",
                    "quantité",
                    "pu",
                    "poste",
                ]

                max_scan = min(len(data), 20)
                for r in range(max_scan):
                    row = data[r]
                    if not row:
                        continue
                    row_text = " ".join(str(cell).lower() for cell in row if cell is not None)
                    keyword_matches = sum(1 for keyword in header_keywords if keyword in row_text)
                    if keyword_matches >= 3:
                        header_row_index = r
                        start_index = r + 1
                        break

                if header_row_index == -1 and data:
                    first_row = [str(cell).lower() for cell in data[0] if cell is not None]
                    if any("designation" in c or "poste" in c for c in first_row):
                        start_index = 1

            designation_col_index = (col_designation_param - 1) if col_designation_param and col_designation_param > 0 else 1
            type_col_index = (col_type_param - 1) if col_type_param and col_type_param > 0 else None
            unite_col_index = (col_unite_param - 1) if col_unite_param and col_unite_param > 0 else 2
            qte_col_index = (col_qte_param - 1) if col_qte_param and col_qte_param > 0 else 3
            pu_col_index = (col_pu_param - 1) if col_pu_param and col_pu_param > 0 else 4
            prix_total_col_index = (col_prix_total_param - 1) if col_prix_total_param and col_prix_total_param > 0 else None

            index_column = designation_col_index if designation_col_index is not None and designation_col_index >= 0 else 0
            name_col_index = type_col_index

            def _clean_str(cell):
                if cell is None:
                    return ""
                # Handle pandas NaN / float('nan')
                if isinstance(cell, float) and math.isnan(cell):
                    return ""
                text = str(cell).strip()
                if text.lower() == "nan":
                    return ""
                return text

            for i in range(start_index, len(data)):
                row = data[i]
                if not row or len(row) == 0:
                    continue

                col0 = _clean_str(row[0]) if len(row) > 0 else ""

                if name_col_index is not None and name_col_index >= 0 and name_col_index < len(row):
                    raw_name = row[name_col_index]
                    col1 = _clean_str(raw_name)
                else:
                    col1 = ""

                if type_col_index is not None and type_col_index >= 0 and type_col_index < len(row):
                    raw_type = row[type_col_index]
                    col_type_value = _clean_str(raw_type)
                else:
                    col_type_value = ""

                if unite_col_index is not None and unite_col_index < len(row) and unite_col_index >= 0:
                    raw_unite = row[unite_col_index]
                    col2 = _clean_str(raw_unite)
                else:
                    col2 = ""

                col3 = row[qte_col_index] if qte_col_index is not None and 0 <= qte_col_index < len(row) else None
                col4 = row[pu_col_index] if pu_col_index is not None and 0 <= pu_col_index < len(row) else None
                col_total = (
                    row[prix_total_col_index]
                    if prix_total_col_index is not None and 0 <= prix_total_col_index < len(row)
                    else None
                )

                if not col_type_value:
                    continue

                if index_column is not None and 0 <= index_column < len(row):
                    raw_index = row[index_column]
                    index_value = _clean_str(raw_index) or col0
                else:
                    index_value = col0

                has_index = bool(index_value)
                has_name = bool(col1)

                is_ouvrage = False
                is_bloc = col_type_value.lower() == "bloc"
                is_article = has_index or has_name

                if is_bloc:
                    # Create a bloc entry
                    qte_value = _safe_float(col3)
                    pu_value = _safe_float(col4)
                    total_value = _safe_float(col_total)

                    current_bloc = {
                        "index": index_value,
                        "designation": index_value or "",
                        "name": col1 or "",
                        "excelType": "bloc",
                        "unite": col2 or "",
                        "qte": qte_value,
                        "pu": pu_value,
                        "prixTotal": total_value,
                    }
                    
                    # Add bloc to current ouvrage or create a default ouvrage
                    if current_ouvrage is None:
                        current_ouvrage = {
                            "name": "Ouvrage Général",
                            "designation": "",
                            "articles": [],
                        }
                        lot_data["ouvrages"].append(current_ouvrage)
                    
                    current_ouvrage["articles"].append(current_bloc)
                    
                elif is_article:
                    if current_ouvrage is None:
                        current_ouvrage = {
                            "name": "Ouvrage Général",
                            "designation": "",
                            "articles": [],
                        }
                        lot_data["ouvrages"].append(current_ouvrage)

                    qte_value = _safe_float(col3)
                    pu_value = _safe_float(col4)
                    total_value = _safe_float(col_total)

                    current_ouvrage["articles"].append(
                        {
                            "index": index_value,
                            "designation": index_value or "",
                            "name": col1 or "",
                            "excelType": col_type_value,
                            "unite": col2 or "",
                            "qte": qte_value,
                            "pu": pu_value,
                            "prixTotal": total_value,
                        }
                    )

            if lot_data["ouvrages"]:
                parsed_data.append(lot_data)

        if preview:
            return parsed_data[:5]
        return parsed_data
    except Exception as e:
        raise Exception(f"Failed to parse Excel: {str(e)}")
