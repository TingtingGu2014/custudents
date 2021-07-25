package cu.students;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import cu.students.entities.StudentEnrollment;
import cu.students.exceptions.CsvHeadingMissingException;
import cu.students.exceptions.CsvToMapIndexNotExistException;
import cu.students.exceptions.GetEnrollmentFromApplsAndDeptsException;
import cu.students.exceptions.MissingCsvRowIdValException;

public class StudentEnrollmentProcessor {
	
	private static final Logger logger = LogManager.getLogger(StudentEnrollmentProcessor.class);
	
	public static List<StudentEnrollment> getEnrollmentFromApplsAndDepts (Map<String, Map<String, String>> studentAppl, Map<String, Map<String, String>> studentDept, Config config) 
			throws NumberFormatException, ParseException, GetEnrollmentFromApplsAndDeptsException {
		
		Validate.notNull(studentAppl);
		Validate.notNull(studentDept);
		
		List<StudentEnrollment> enrollmentList = new ArrayList<>();
		
		try {
			for(String studentId: studentAppl.keySet()) {
				if(null != studentDept.get(studentId)) {
					Map<String, String> studentApplMap = studentAppl.get(studentId);
					Map<String, String> studentDeptMap = studentDept.get(studentId);
					String department = studentDeptMap.get(config.getProperty("departments.column.department"));
	    			StudentEnrollment enrollment = new StudentEnrollment(new StudentEnrollment.StudentEnrollmentBuilder(studentId)
	    					.setApplDate(new SimpleDateFormat("yyyy-MM-dd").parse(studentApplMap.get(config.getProperty("applications.column.applDate"))))
	    					.setApplStatus(Integer.parseInt(studentApplMap.get(config.getProperty("applications.column.status"))))
	    					.setDepartment(department)
	    					.setCourse(config.getDeptCourseMap().get(department)));
	    			enrollmentList.add(enrollment);
	    		}
	    		else {
	    			logger.warn("Cannot find student department information, id="+studentId);
	    		}
				
			}
			return enrollmentList;
		}
		catch(Exception ex) {
			throw new GetEnrollmentFromApplsAndDeptsException("Error: GetEnrollmentFromApplsAndDeptsException");
		}  
	}
	
	public static List<StudentEnrollment> getStudentsEnrollments(Config config) {
		Map<String, Map<String, String>> applMap;
		try {
			applMap = EnrollUtils.getMapMapFromCSV(EnrollUtils.APPLICARIONS_CSV_PROPERTY, config.getProperty(EnrollUtils.APPLICATIONS_CSV_IN_COLUMN));
			Map<String, Map<String, String>> departmentsMap = EnrollUtils.getMapMapFromCSV(EnrollUtils.DEPARTMENTS_CSV_PROPERTY, config.getProperty(EnrollUtils.DEPARTMENTS_CSV_IN_COLUMN));
	    	List<StudentEnrollment> enrollList = StudentEnrollmentProcessor.getEnrollmentFromApplsAndDepts(applMap, departmentsMap, config);
	    	return enrollList;
		} catch (IOException | CsvHeadingMissingException | CsvToMapIndexNotExistException
				| MissingCsvRowIdValException e) {
			logger.error(e.getMessage());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
		} catch (ParseException e) {
			logger.error(e.getMessage());
		} catch (GetEnrollmentFromApplsAndDeptsException e) {
			logger.error(e.getMessage());
		}
    	return null;
	}
	
	public static void saveStudentsEnrollmentsIntoFile(Config config, List<StudentEnrollment> enrollList, File file) {
		ObjectMapper mapper = new ObjectMapper();
    	try {
			mapper.writeValue(file, enrollList);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
 