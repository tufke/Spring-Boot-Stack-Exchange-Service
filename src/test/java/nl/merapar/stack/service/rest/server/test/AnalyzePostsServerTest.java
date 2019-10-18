package nl.merapar.stack.service.rest.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import nl.merapar.stack.StackServiceApplication;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsDetailsSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsRequestSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = StackServiceApplication.class)
public class AnalyzePostsServerTest extends AbstractServerTest {

	@Autowired
    protected TestRestTemplate restTemplate;
	
    @LocalServerPort
    int randomServerPort;
	
	private String server;
	
	@Before
	public void init() {
		server = new StringBuilder().append("http://localhost:").append(randomServerPort).append("/").toString();
	}
    
	/**
	 * Call the rest resource on a running server and test the whole chain.
	 * the call to the external http url is the only one mocked.
	 */
    @Test
    public void testAnalyzePosts() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:46:22.083";
		Integer totalAcceptedPosts = 3;
		Integer totalPosts = 3;
		Integer avgScore = 18;
		
		AnalyzePostsResponseSO expectedResult = createResponseSO(firstPost, lastPost, totalAcceptedPosts, totalPosts, avgScore);

    	// create a POST request
    	String uri = "stack/posts/analyze";
    	AnalyzePostsRequestSO body = new AnalyzePostsRequestSO("http://localhost:9000/xml/posts/posts.xml");
		RequestEntity<AnalyzePostsRequestSO> request = RequestEntity
				.post(new URI(server + uri))
				.accept(MediaType.APPLICATION_JSON)
				.body(body);
		ResponseEntity<AnalyzePostsResponseSO> response = doRequest(request, AnalyzePostsResponseSO.class, "xml/posts/posts.xml");
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(expectedResult, response.getBody());

    }
    
	private AnalyzePostsResponseSO createResponseSO(String firstPost, String lastPost, Integer totalAcceptedPosts, Integer totalPosts, Integer avgScore) {
		AnalyzePostsDetailsSO details = 
			AnalyzePostsDetailsSO.builder()
				.firstPost(LocalDateTime.parse(firstPost, dateTimeFormatter))
				.lastPost(LocalDateTime.parse(lastPost, dateTimeFormatter))
				.totalAcceptedPosts(new BigInteger(totalAcceptedPosts.toString()))
				.totalPosts(new BigInteger(totalPosts.toString()))
				.avgScore(avgScore)
				.build();
			
		
		return AnalyzePostsResponseSO.builder()
				.details(details)
				.build();
	}	    
}
