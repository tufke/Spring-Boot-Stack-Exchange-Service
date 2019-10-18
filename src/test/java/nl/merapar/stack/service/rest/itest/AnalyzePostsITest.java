package nl.merapar.stack.service.rest.itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import nl.merapar.stack.StackServiceApplication;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsDetailsSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsRequestSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;


/**
 * Call the rest resource on a running server and test the whole chain.
 * the call to the external http url is not mocked.
 * to make this work you might have to configure JVM arguments on the test runner to prevent getting 
 * a UnknownHostException
 * i.e.
 * -Djava.net.useSystemProxies=true   (set Proxy)
 * 
 * in case of using https the certificate of the server you request xml from should be in the truststore 
 * configured in application-integration.properties. The certificate must be signed by an Authorized CA 
 * in the top of the certificate chain and can be downloaded from your browser when you are not behind a proxy.
 * If you are behind a proxy you will need a root company certificate.
 * 
 * If you want to test without a certificate you can enable http.client.ssl.accept-all-trust-store=true 
 * on the @SpringBootTest annotation to make sure all certificates are accepted in case of https calls.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"http.client.ssl.accept-all-trust-store=false"}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = StackServiceApplication.class)
@TestPropertySource(locations = "classpath:application-integration.properties")
public class AnalyzePostsITest extends AbstractIntegrationTest {

	@Autowired
    protected TestRestTemplate restTemplate;
	
	private String server = "http://localhost:8080/";
    

    @Test
    public void testAnalyzePosts_http() throws Exception {
		//Expect result
		String firstPost = "2015-07-14T18:39:27.757";
		String lastPost = "2015-09-14T12:46:52.053";
		Integer totalPosts = 80;
		Integer totalAcceptedPosts = 7;
		Integer avgScore = 2;
		
		AnalyzePostsResponseSO expectedResult = createResponseSO(firstPost, lastPost, totalAcceptedPosts, totalPosts, avgScore);

    	// create a POST request
    	String uri = "stack/posts/analyze";
    	AnalyzePostsRequestSO body = new AnalyzePostsRequestSO("http://s3-eu-west-1.amazonaws.com/merapar-assessment/arabic-posts.xml");
		RequestEntity<AnalyzePostsRequestSO> request = RequestEntity
				.post(new URI(server + uri))
				.accept(MediaType.APPLICATION_JSON)
				.body(body);
		ResponseEntity<AnalyzePostsResponseSO> response = doRequest(request, AnalyzePostsResponseSO.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(expectedResult, response.getBody());

    }
    
    /**
     * 
     * To test with certificate put a valid certificate signed by an authorized CA for the url you want to test in src/main/resources/keystore/stackservice.truststore
     * and set http.client.ssl.accept-all-trust-store=false in the @SpringBootTest annotation. 
     * The certificate i can download from my browser for the test URL is a self-signed certificate which gets rejected as there is no authorized CA in the certificate chain. 
     */
    @Test
    public void testAnalyzePosts_https() throws Exception {
		//Expect result
		String firstPost = "2015-07-14T18:39:27.757";
		String lastPost = "2015-09-14T12:46:52.053";
		Integer totalPosts = 80;
		Integer totalAcceptedPosts = 7;
		Integer avgScore = 2;
		
		AnalyzePostsResponseSO expectedResult = createResponseSO(firstPost, lastPost, totalAcceptedPosts, totalPosts, avgScore);

    	// create a POST request
    	String uri = "stack/posts/analyze";
    	AnalyzePostsRequestSO body = new AnalyzePostsRequestSO("https://s3-eu-west-1.amazonaws.com/merapar-assessment/arabic-posts.xml");
		RequestEntity<AnalyzePostsRequestSO> request = RequestEntity
				.post(new URI(server + uri))
				.accept(MediaType.APPLICATION_JSON)
				.body(body);
		ResponseEntity<AnalyzePostsResponseSO> response = doRequest(request, AnalyzePostsResponseSO.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(expectedResult, response.getBody());

    }    
    
    @Test
    public void testAnalyzePosts_file() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:46:22.083";
		Integer totalAcceptedPosts = 3;
		Integer totalPosts = 3;
		Integer avgScore = 18;
		
		AnalyzePostsResponseSO expectedResult = createResponseSO(firstPost, lastPost, totalAcceptedPosts, totalPosts, avgScore);

    	// create a POST request
    	String uri = "stack/posts/analyze";
    	URL url = getClass().getClassLoader().getResource("xml/posts/posts.xml");
    	AnalyzePostsRequestSO body = new AnalyzePostsRequestSO(url.toString());
		RequestEntity<AnalyzePostsRequestSO> request = RequestEntity
				.post(new URI(server + uri))
				.accept(MediaType.APPLICATION_JSON)
				.body(body);
		ResponseEntity<AnalyzePostsResponseSO> response = doRequest(request, AnalyzePostsResponseSO.class);
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
