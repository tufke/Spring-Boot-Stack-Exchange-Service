package nl.merapar.stack.service.rest.resource.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import nl.merapar.stack.service.rest.problem.Problem;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsDetailsSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsRequestSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;
import nl.merapar.stack.service.rest.resource.operation.AnalyzePostsOperation;


public class AnalyzePostsResourceTest extends AbstractResourceTest {

    
    @MockBean 
    private AnalyzePostsOperation analyzePostsoperation;
    
    protected String getResourceURI() {
    	return "/stack/posts/analyze";
    }
    
    @Test
    public void testEmptyInput() throws Exception {
        
    	//request
    	AnalyzePostsRequestSO request = null;
    	
    	//response
		Problem problem = 
			Problem.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.detail("Required request body is missing")
				.build();
    	
        expect(request, problem);
    }
    
    @Test
    public void testURL_Empty() throws Exception {
      
    	//request
    	AnalyzePostsRequestSO request =
	    	AnalyzePostsRequestSO.builder()
	    		.url("")
	    		.build();
    	
    	//response
		Problem problem = 
			Problem.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.detail(PROBLEM_DETAIL_VALIDATION_FAILED)
				.violations(createViolation("url"))
				.build();
    	
        expect(request, problem);
    } 
    
    @Test
    public void testURL_no_protocol() throws Exception {
      
    	//request
    	AnalyzePostsRequestSO request =
	    	AnalyzePostsRequestSO.builder()
	    		.url("posts.xml")
	    		.build();
    	
    	//response
		Problem problem = 
			Problem.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.detail(PROBLEM_DETAIL_VALIDATION_FAILED)
				.violations(createViolation("url"))
				.build();
    	
        expect(request, problem);
    }     
    
    @Test
    public void testSuccess_file() throws Exception {
    	//request
    	AnalyzePostsRequestSO request = getValidFileRequest();
    	
    	//response
    	AnalyzePostsDetailsSO details = 
    			AnalyzePostsDetailsSO.builder()
    				.firstPost(LocalDateTime.now())
    				.lastPost(LocalDateTime.now())
    				.avgScore(10)
    				.totalAcceptedPosts(new BigInteger("3"))
    				.totalPosts(new BigInteger("3"))
    				.build();
    	
    	AnalyzePostsResponseSO response = 
    			AnalyzePostsResponseSO.builder()
    				.details(details)
    				.build();
    	
    	given(this.analyzePostsoperation.operate(request)).willReturn(response);
    	expect(request, response);
    } 
    
    @Test
    public void testSuccess_http() throws Exception {
    	//request
    	AnalyzePostsRequestSO request = getValidHttpRequest();
    	
    	//response
    	AnalyzePostsDetailsSO details = 
    			AnalyzePostsDetailsSO.builder()
    				.firstPost(LocalDateTime.now())
    				.lastPost(LocalDateTime.now())
    				.avgScore(10)
    				.totalAcceptedPosts(new BigInteger("3"))
    				.totalPosts(new BigInteger("3"))
    				.build();
    	
    	AnalyzePostsResponseSO response = 
    			AnalyzePostsResponseSO.builder()
    				.details(details)
    				.build();
    	
    	given(this.analyzePostsoperation.operate(request)).willReturn(response);
    	expect(request, response);
    } 
    
    protected ResultActions expect(AnalyzePostsRequestSO request, AnalyzePostsResponseSO expected) throws Exception {
    	
    	ResultActions result = mvc.perform(createPost(request));
    	result.andExpect(status().is(HttpStatus.OK.value()));
    	result.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8.toString()));
   		
   		String contentAsString = result.andReturn().getResponse().getContentAsString();
   		AnalyzePostsResponseSO response = jacksonObjectMapper.readValue(contentAsString, AnalyzePostsResponseSO.class);
   		
   		assertTrue(response.getAnalyseDate().isBefore(LocalDateTime.now()));
   		assertNotNull(response.getDetails());
   		assertNotNull(expected.getDetails());
   		assertNotNull(response.getDetails().getFirstPost());
   		assertNotNull(expected.getDetails().getFirstPost());
   		assertNotNull(response.getDetails().getLastPost());
   		assertNotNull(expected.getDetails().getLastPost());
   		assertTrue(response.getDetails().getFirstPost().isBefore(response.getDetails().getLastPost()) 
   					|| response.getDetails().getFirstPost().isEqual(response.getDetails().getLastPost()));
   		assertEquals(expected.getDetails().getFirstPost(), response.getDetails().getFirstPost());
   		assertEquals(expected.getDetails().getLastPost(), response.getDetails().getLastPost());
   		assertEquals(expected.getDetails().getAvgScore(), response.getDetails().getAvgScore());
   		assertEquals(expected.getDetails().getTotalAcceptedPosts(), response.getDetails().getTotalAcceptedPosts());
   		assertEquals(expected.getDetails().getTotalPosts(), response.getDetails().getTotalPosts());

    	return result;
    }
    
    private AnalyzePostsRequestSO getValidFileRequest() {
    	//request
    	AnalyzePostsRequestSO request =
	    	AnalyzePostsRequestSO.builder()
	    		.url("file:posts.xml")
	    		.build();
    	
    	return request;
    }

    private AnalyzePostsRequestSO getValidHttpRequest() {
    	//request
    	AnalyzePostsRequestSO request =
	    	AnalyzePostsRequestSO.builder()
	    		.url("http://www.someting.nl/posts.xml")
	    		.build();
    	
    	return request;
    }

}
