from .base_parser import BaseParser

class DPGFParser(BaseParser):
    """
    Concrete implementation of the parser for DPGF Excel files.
    """

    def load_data(self, file_path: str):
        print(f"Loading Excel file from {file_path}")
        # Simulation of loading
        return {"content": "dpgf_dummy_data"}

    def validate_data(self, data):
        print("Checking if 'content' key exists...")
        return "content" in data

    def parse_content(self, data):
        print("Parsing DPGF rows...")
        # Simulation of parsing logic
        return [{"id": 1, "item": "Foundation"}, {"id": 2, "item": "Walls"}]

    # process_file() is inherited from BaseParser template
