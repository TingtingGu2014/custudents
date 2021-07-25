package cu.students;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import cu.students.entities.StudentEnrollment;
import cu.students.exceptions.CsvHeadingMissingException;
import cu.students.exceptions.CsvToMapIndexNotExistException;
import cu.students.exceptions.GetEnrollmentFromApplsAndDeptsException;
import cu.students.exceptions.MissingCsvRowIdValException;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger logger = LogManager.getLogger(App.class);
	
    public static void main( String[] args )
    {
        Config config = Config.getInstance();
        try {
        	List<StudentEnrollment> enrollList = StudentEnrollmentProcessor.getStudentsEnrollments(config);
        	File file = new File(EnrollUtils.class.getClassLoader().getResource(config.getProperty("result.log")).getPath());
        	StudentEnrollmentProcessor.saveStudentsEnrollmentsIntoFile(config, enrollList, file);
        	System.out.println(enrollList);
        }
        catch (Exception e) {
			logger.error(e.getMessage());
		}
    }
}
