package cu.students;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
			throws NumberFormatException, GetEnrollmentFromApplsAndDeptsException {
		
		Validate.notNull(studentAppl);
		Validate.notNull(studentDept);
		
		List<StudentEnrollment> enrollmentList = new ArrayList<>();
		Set<String> deptStudentIds = studentDept.keySet();
		
		try {
			for(String studentId: studentAppl.keySet()) {
				if(null != studentDept.get(studentId)) {
					Map<String, String> studentApplMap = studentAppl.get(studentId);
					Map<String, String> studentDeptMap = studentDept.get(studentId);
					
					String department = studentDeptMap.get(config.getProperty("departments.column.department"));
					String course = config.getDeptCourseMap().get(department);
					if(StringUtils.isBlank(department) || StringUtils.isBlank(course)) {
						logger.error("Student ID: " + studentId + " has wrong department information and is NOT enrolled.");
						continue;
					}
					
					Date applDate = null;
					try {
						applDate = new SimpleDateFormat("yyyy-MM-dd").parse(studentApplMap.get(config.getProperty("applications.column.applDate")));
						if(null == applDate) {
							throw new Exception("Null Applicaiton Date after parsing.");
						}
					}
					catch(Exception e) {
						logger.error("Student ID: " + studentId + " is NOT enrolled due to invalid Application Date: " + e);
						continue;
					}
					
					int status = 0;
					try {
						status = Integer.parseInt(studentApplMap.get(config.getProperty("applications.column.status")));
					}
					catch(Exception e) {
						logger.error("Student ID: " + studentId + " is NOT enrolled due to invalid Status information: " + e.getMessage());
						continue;
					}
					
	    			StudentEnrollment enrollment = new StudentEnrollment(new StudentEnrollment.StudentEnrollmentBuilder(studentId)
	    					.setApplDate(applDate)
	    					.setApplStatus(status)
	    					.setDepartment(department)
	    					.setCourse(course));
	    			logger.info("Student ID: " + studentId + " enrolled in Department: " + department + ", with course: " + course + ", with date: " + EnrollUtils.getDateString(applDate));
	    			enrollmentList.add(enrollment);
	    			deptStudentIds.remove(studentId);
	    		}
	    		else {
	    			logger.error("Cannot find student department information, id="+studentId);
	    		}
				
			}
			
			if(deptStudentIds.size() > 0) {
				for(String studentId : deptStudentIds) {
					logger.error("Cannot find student application information, id="+studentId);
				}
			}
			return enrollmentList;
		}
		catch(Exception ex) {
			throw new GetEnrollmentFromApplsAndDeptsException("Error: GetEnrollmentFromApplsAndDeptsException");
		}  
	}
	
	public static List<StudentEnrollment> getStudentsEnrollments(Config config) {
		
		try {
			Map<String, Map<String, String>> applMap = EnrollUtils.getMapMapFromCSV(EnrollUtils.APPLICARIONS_CSV_PROPERTY, config.getProperty(EnrollUtils.APPLICATIONS_CSV_IN_COLUMN));
			Map<String, Map<String, String>> departmentsMap = EnrollUtils.getMapMapFromCSV(EnrollUtils.DEPARTMENTS_CSV_PROPERTY, config.getProperty(EnrollUtils.DEPARTMENTS_CSV_IN_COLUMN));
	    	List<StudentEnrollment> enrollList = StudentEnrollmentProcessor.getEnrollmentFromApplsAndDepts(applMap, departmentsMap, config);
	    	return enrollList;
		} catch (IOException | CsvHeadingMissingException | CsvToMapIndexNotExistException
				| MissingCsvRowIdValException e) {
			logger.error(e.getMessage());
		} catch (NumberFormatException e) {
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
	
	public boolean validateStudentEnrollment (StudentEnrollment studentEnrollment) {
		return (StringUtils.isBlank(studentEnrollment.getStudentId())
				|| StringUtils.isBlank(studentEnrollment.getCourse())
				|| StringUtils.isBlank(studentEnrollment.getDepartment())
				|| null == studentEnrollment.getApplDate()
				|| studentEnrollment.getApplStatus() == 0);
	}
}
 