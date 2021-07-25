package cu.students;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cu.students.entities.StudentEnrollment;
import cu.students.exceptions.CsvHeadingMissingException;
import cu.students.exceptions.CsvToMapIndexNotExistException;
import cu.students.exceptions.MissingCsvRowIdValException;
 
public class EnrollUtils {
	
	private static final Logger logger = LogManager.getLogger(EnrollUtils.class);
	
	public static Properties prop = new Properties();
	public static String CONFIGS_FILE_NAME = "configs.properties";
	public static String APPLICARIONS_CSV_PROPERTY = "applications.csv";
	public static String DEPARTMENTS_CSV_PROPERTY = "departments.csv";
	public static String APPLICATIONS_CSV_IN_COLUMN = "applications.column.studentId";
	public static String DEPARTMENTS_CSV_IN_COLUMN = "departments.column.studentId";
	
	public static InputStream readSourceFileStream(String path) {
		return EnrollUtils.class.getClassLoader().getResourceAsStream(path);
	}
	
	public static Properties getConfigProps() throws IOException {
		
		prop.load(readSourceFileStream(CONFIGS_FILE_NAME));
		return prop;
	}
	
	public static Map<String, Map<String, String>> getMapMapFromCSV(String fileName, String indexColumn) throws IOException, 
																	CsvHeadingMissingException, CsvToMapIndexNotExistException, MissingCsvRowIdValException 
	{
		Map<String, Map<String, String>> csvMapMap = new HashMap<>();
		InputStream in = EnrollUtils.class.getClassLoader().getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		List<String> headingList = new ArrayList<>();
		String line = null;
		boolean indexColumnExists = false;
		int index = 0; 

        while ((line = br.readLine()) != null) {
        	String[] values = line.split(",");
        	if(index ==0) {
            	for(String head : values){
            		
            		if(StringUtils.isBlank(head)) {
            			throw new CsvHeadingMissingException("Invalid csv file: missing csv file heading");
            		}
            		
            		if(head.equals(indexColumn)) {
            			indexColumnExists = true;
            		}
            		
            		headingList.add(head);
            	}
            	if(!indexColumnExists) {
        			throw new CsvToMapIndexNotExistException("Invalid csv file: converting csv to map while missing index column: " + indexColumn);
        		}
        	}
        	else {
        		Map<String, String> row = new HashMap<String, String>();
        		String indexValue = "";
        		int colIndex = 0;
        		for(String value : values){
        			String head = headingList.get(colIndex);
            		row.put(head, value);
            		if(head.equals(indexColumn)) {
            			if(StringUtils.isBlank(value)) {
            				throw new MissingCsvRowIdValException("Invalid csv file: missing csv row ID column value.");
            			}
            			indexValue = value;
            		}
            		colIndex++;
            	}
        		if(StringUtils.isNotBlank(indexValue)) {
        			csvMapMap.put(indexValue, row);
        		}
        		else {
        			logger.warn("converting csv to map: missing index column value: ", row);
        		}
        	}
        	index++;
        }
        in.close();
        br.close();
        return csvMapMap;
	}
	
	public static String getDateString(Date date) {
		
		String isoDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(isoDatePattern);
		String dateString = simpleDateFormat.format(date);
		return dateString;
	}
	
	/**
	 * 
	 * @param props : Properties
	 * @param key : String
	 * 
	 * In properties file, all map data is stored in JSON format for parsing
	 * 
	 * @return : Map<String, String>
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public static Map<String, String> getMapProperty(Properties props, String key) throws JsonMappingException, JsonProcessingException {
		Validate.notNull(props);
		Validate.notNull(key);
		
		String jsonValue = props.getProperty(key);
		if(StringUtils.isNotBlank(jsonValue)) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(jsonValue, Map.class);
			return map;
		}
		return null;
	}
	
	public static String readFileToString(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();

		return stringBuilder.toString();
	}
	
	public static boolean compareStudentEnrollmentLists(List<StudentEnrollment> list1, List<StudentEnrollment> list2) {
		Assert.assertNotNull(list1);
		Assert.assertNotNull(list2);
		
		if(list1.size() != list2.size()) {
			return false;
		}
		
		for(int i = 0; i < list1.size(); i++) {
			if(!list1.get(i).equals(list2.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
