from fastapi import FastAPI, UploadFile, File, HTTPException, Form
import uvicorn
import os
from dotenv import load_dotenv
from services.excel_parser import parse_dpgf, get_sheet_names, get_sheet_rows

load_dotenv()

app = FastAPI(title="EcoPilot Import Service")

import logging
import math
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse


def clean_data_for_json(data):
    """Clean data by converting NaN and infinity values to None for JSON serialization."""
    if isinstance(data, dict):
        return {key: clean_data_for_json(value) for key, value in data.items()}
    elif isinstance(data, list):
        return [clean_data_for_json(item) for item in data]
    elif isinstance(data, float):
        if math.isnan(data) or math.isinf(data):
            return None
        return data
    else:
        return data


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request, exc):
    logging.error(f"Validation error for {request.url}: {exc}")
    return JSONResponse(
        status_code=422,
        content={"detail": exc.errors(), "message": "Validation Error"},
    )


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/api/projets/preview-dpgf")
async def preview_dpgf(file: UploadFile = File(...)):
    if not file.filename.endswith((".xlsx", ".xls")):
        raise HTTPException(status_code=400, detail="Invalid file format. Please upload Excel file.")

    try:
        content = await file.read()
        sheets = get_sheet_names(content, file.filename)
        return {"success": True, "sheets": sheets}
    except Exception as e:
        logging.exception("Error while previewing DPGF file")
        return {"success": False, "message": str(e)}


@app.post("/api/projets/preview-dpgf-sheet")
async def preview_dpgf_sheet(file: UploadFile = File(...), sheetName: str = Form(...)):
    try:
        content = await file.read()
        rows = get_sheet_rows(content, sheetName, file.filename)
        return {"success": True, "rows": rows}
    except Exception as e:
        logging.exception("Error while previewing DPGF sheet")
        return {"success": False, "message": str(e)}


@app.post("/api/projets/parse-dpgf")
async def process_dpgf(
    file: UploadFile = File(...),
    selectedSheets: str = Form(None),
    headerRow: str = Form(None),
    colDesignation: str = Form(None),
    colType: str = Form(None),
    colUnite: str = Form(None),
    colQte: str = Form(None),
    colPu: str = Form(None),
    colPrixTotal: str = Form(None),
):
    if not file.filename.endswith((".xlsx", ".xls")):
        raise HTTPException(status_code=400, detail="Invalid file format")

    try:
        content = await file.read()
        data = parse_dpgf(
            content,
            file.filename,
            selected_sheets=selectedSheets,
            header_row=headerRow,
            col_designation=colDesignation,
            col_type=colType,
            col_unite=colUnite,
            col_qte=colQte,
            col_pu=colPu,
            col_prix_total=colPrixTotal,
        )
        # Clean the data to ensure JSON compliance
        cleaned_data = clean_data_for_json(data)
        return {"success": True, "data": cleaned_data}
    except Exception as e:
        logging.exception("Error while parsing DPGF file")
        return {"success": False, "message": str(e)}


if __name__ == "__main__":
    port = int(os.getenv("PORT", 8088))
    uvicorn.run(app, host="0.0.0.0", port=port)
