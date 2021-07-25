package cu.students.exceptions;

public class CsvHeadingMissingException extends Exception {
	public CsvHeadingMissingException(String errorMessage) {
        super(errorMessage);
    }
}
