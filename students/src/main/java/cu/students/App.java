package cu.students;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cu.students.entities.StudentEnrollment;

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
        	final List<StudentEnrollment> enrollList = StudentEnrollmentProcessor.getStudentsEnrollments(config);
        	final File file = new File(EnrollUtils.class.getClassLoader().getResource(config.getProperty("result.log")).getPath());
        	StudentEnrollmentProcessor.saveStudentsEnrollmentsIntoFile(config, enrollList, file);
        }
        catch (Exception e) {
			logger.error(e.getMessage());
		}
    }
}
