package cu.students;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Config {
	
	private Properties props;
	private Map<String, String> deptCourseMap;
	
	private static class SingletonHelper {
		
		public static final Config INSTANCE;

	    static {
	        try {
	        	INSTANCE = new Config();
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }
	    }
    }
    
    private Config() throws IOException {
    	props = EnrollUtils.getConfigProps();
    	deptCourseMap = EnrollUtils.getMapProperty(props, "departments.map.courses");
    }
    
    public static Config getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    public String getProperty(String propertyName) {
    	return props.getProperty(propertyName);
    }
    
    public Map<String, String> getDeptCourseMap() {
    	return deptCourseMap;
    }
}
