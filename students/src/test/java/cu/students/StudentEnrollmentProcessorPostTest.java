package cu.students;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import cu.students.entities.StudentEnrollment;

/**
 * Unit test for cu.students.
 */

public class StudentEnrollmentProcessorPostTest
{
	private ClientAndServer mockServer;
	private int port = 1080;
	private File logFile1;
	private String enrollments = null;
	private Config config;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() throws IOException, URISyntaxException {
    	config = Config.getInstance();
        mockServer = ClientAndServer.startClientAndServer(port);
        String resultJsonPath = StudentEnrollmentProcessorPostTest.class.getClassLoader().getResource(config.getProperty("result.log")).toURI().getPath();
        logFile1 = new File(resultJsonPath);
        enrollments = EnrollUtils.readFileToString(resultJsonPath);
    }

    @After
    public void tearDownServer() {
        mockServer.stop();
    }
    
    @Test
    public void testCsvToEnrollmentList() throws Exception {
    	final List<StudentEnrollment> enrollList = StudentEnrollmentProcessor.getStudentsEnrollments(config);
    	final ObjectMapper mapper = new ObjectMapper();
    	final File logFile2 = new File(StudentEnrollmentProcessorPostTest.class.getClassLoader().getResource(config.getProperty("result2.log")).toURI().getPath());
    	StudentEnrollmentProcessor.saveStudentsEnrollmentsIntoFile(config, enrollList, logFile2);
    	mapper.writeValue(logFile2, enrollList);
    	assertTrue(FileUtils.contentEquals(logFile1, logFile2));
    }

    @Test
    public void testPostEnrollments() throws ParseException, IOException {
    	
    	mockServer.when(HttpRequest.request().withMethod("POST").withPath("/enrollments"))
		.respond(HttpResponse.response().withStatusCode(200).withBody(enrollments));

    	org.apache.http.HttpResponse response = sendPostRequest();
		assertEquals(enrollments, EntityUtils.toString(response.getEntity()));
    }
    
    @Test
    public void studentEnrollmentProcessorTest() {
    	
    	try {
	    	testCsvToEnrollmentList();
	    	testPostEnrollments();
    	}
    	catch(Exception e) {
    		assertNull(e);
    	}
    }

    private org.apache.http.HttpResponse sendPostRequest() {
        final String url = "http://localhost:"+port+"/enrollments";
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(url);
        post.setHeader("Content-type", "application/json");
        org.apache.http.HttpResponse response=null;

        try {
            StringEntity stringEntity = new StringEntity("{username: 'foo', password: 'bar'}");
            post.getRequestLine();
            post.setEntity(stringEntity);
            response=client.execute(post);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
