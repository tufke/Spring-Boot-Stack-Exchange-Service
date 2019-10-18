package nl.merapar.stack.service.rest.resource.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import nl.merapar.stack.StackServiceApplication;
import nl.merapar.stack.service.rest.problem.ConstraintViolation;
import nl.merapar.stack.service.rest.problem.Problem;


/**
 * 
 * approach is to not start the server (WebEnvironment.MOCK) but test only the layer below that, 
 * where Spring handles the incoming HTTP request and hands it off to the rest controller. 
 * That way, almost the full stack is used, and the code will be called exactly 
 * the same way as if it was processing a real HTTP request, but without the cost 
 * of starting the server. 
 * 
 * To do that Spring’s MockMvc is used, and we inject it by using the @AutoConfigureMockMvc 
 * annotation on the test case.
 */
@RunWith(SpringRunner.class) //Provides a bridge between Spring and JUnit so annotations get processed
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = StackServiceApplication.class) //create an application context from class annotated with @SpringBootApplication, it searches for it upwards through the package structure
@AutoConfigureMockMvc //add a MockMvc instance to the application context.
public abstract class AbstractResourceTest {

	protected static final String PROBLEM_DETAIL_VALIDATION_FAILED = "Validation failed";
	
    @Autowired 
    protected MockMvc mvc;
	
	@Autowired
	protected ObjectMapper jacksonObjectMapper;        
	
	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    
	protected abstract String getResourceURI();
	
    protected ResultActions expect(Object request, Problem problem) throws Exception {
    	
    	ResultActions result = mvc.perform(createPost(request));

    	if(problem.getStatus() != null) {
    		result.andExpect(status().is(problem.getStatus()));
    	}
    	
    	result.andExpect(jsonPath("$.headers.Content-Type[0]", equalTo(Problem.PROBLEM_JSON)));
    	
    	String contentAsString = result.andReturn().getResponse().getContentAsString();
    	JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
    	JSONObject json = (JSONObject) parser.parse(contentAsString);
    	String body = json.getAsString("body");
    	Problem response = jacksonObjectMapper.readValue(body, Problem.class);
    	
    	if(problem.getDetail() != null) {
    		assertTrue(response.getDetail().contains(problem.getDetail()));
    	}
    	
    	if(!problem.getViolations().isEmpty()) {
    		for(ConstraintViolation violation : problem.getViolations() ) {
    			boolean foundViolation = false;
    			for(ConstraintViolation respViolation : response.getViolations()) {
    				if(respViolation.getField().equals(violation.getField())) {
    					foundViolation = true;
    				}
    			}
    			assertTrue(foundViolation);
    		}
    	}

    	return result;
    }
    
    protected MockHttpServletRequestBuilder createPost(Object request) throws URISyntaxException, JsonProcessingException {
    	MockHttpServletRequestBuilder builder =
    			post(new URI(getResourceURI()))
    			.contentType(MediaType.APPLICATION_JSON)
    			.accept(MediaType.APPLICATION_JSON);
    	
    	if (request != null) {
    		builder = builder.content(jacksonObjectMapper.writeValueAsString(request));
    	}
    	return builder;
    }    
    
    protected List<ConstraintViolation> createViolation(String field) {
    	List<ConstraintViolation> violations = new ArrayList<ConstraintViolation>();
    	violations.add(new ConstraintViolation(field, ""));
    	return violations;
    }
}
