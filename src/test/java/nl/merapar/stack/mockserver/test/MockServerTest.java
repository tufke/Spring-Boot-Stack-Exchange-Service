package nl.merapar.stack.mockserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.netty.handler.codec.http.HttpHeaderNames;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class MockServerTest {

	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this, 9000);
 
	
	@Autowired
    protected TestRestTemplate restTemplate;

	protected MockServerClient mockServerClient;	
	
	
	@Test
	public void testShouldConnectToMockHttpServiceAndGetPostsResponse() throws Exception {
		byte[] fileBytes = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("xml/posts/posts.xml"));
		
		// setting behaviour for test case
		mockServerClient
	    .when(
	    		HttpRequest.request()
	    )
	    .respond(
	    	HttpResponse.response()
	            .withStatusCode(HttpStatus.OK.value())
	            .withReasonPhrase(HttpStatus.OK.getReasonPhrase())
	            .withHeaders(
	                Header.header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_XML.toString()),
	                Header.header(HttpHeaderNames.CONTENT_DISPOSITION.toString(), "form-data; name=\"test.xml\"; filename=\"test.xml\""),
	                Header.header(HttpHeaderNames.CONTENT_LENGTH.toString(), fileBytes.length),
	                Header.header(HttpHeaderNames.CACHE_CONTROL.toString(), "must-revalidate, post-check=0, pre-check=0")
	            )
	            .withBody(BinaryBody.binary(fileBytes))
	    ); 
		
		// create a GET request
		RequestEntity<Void> request = new RequestEntity<>(HttpMethod.GET, URI.create("http://localhost:9000/test.xml"));
		ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {};
		ResponseEntity<String> response = this.restTemplate.exchange(request, typeRef);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		String xmlBody = response.getBody();
		assertNotNull(xmlBody);
		assertTrue(fileBytes.length == xmlBody.length());
		assertTrue(xmlBody.contains("<posts>"));
		
		// verify server has received exactly one request
		mockServerClient.verify(HttpRequest.request("/test.xml"), VerificationTimes.once());
	}	
}
