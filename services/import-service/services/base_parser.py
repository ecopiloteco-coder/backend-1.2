from abc import ABC, abstractmethod

class BaseParser(ABC):
    """
    Template Method Pattern: Defines the skeleton of the parsing algorithm.
    """

    def process_file(self, file_path: str):
        """
        The Template Method. Defines the sequence of steps.
        """
        print(f"Starting process for {file_path}")
        data = self.load_data(file_path)
        if self.validate_data(data):
            parsed_results = self.parse_content(data)
            self.save_results(parsed_results)
            print("Processing completed successfully.")
            return parsed_results
        else:
            print("Validation failed.")
            return None

    @abstractmethod
    def load_data(self, file_path: str):
        pass

    def validate_data(self, data):
        """
        Hook method: Can be overridden, has default implementation.
        """
        print("Validating data structure...")
        return True

    @abstractmethod
    def parse_content(self, data):
        pass

    def save_results(self, results):
        """
        Common step: Sending to DB or printing.
        """
        print(f"Saving {len(results) if results else 0} records...")
