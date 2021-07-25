package cu.students.exceptions;

public class MissingCsvRowIdValException  extends Exception {
	public MissingCsvRowIdValException(String errorMessage) {
        super(errorMessage);
    }
}
