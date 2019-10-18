package nl.merapar.stack.service.rest.server.test;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import io.netty.handler.codec.http.HttpHeaderNames;
import nl.merapar.stack.service.rest.resource.api.AbstractServiceObject;

public class AbstractServerTest {

	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this, 9000);
 
	protected MockServerClient mockServerClient;	
	
	@Autowired
    protected TestRestTemplate restTemplate;

	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	protected <T extends AbstractServiceObject, S extends AbstractServiceObject> ResponseEntity<T> doRequest(RequestEntity<S> request, Class<T> clazz, String pathToXMLFile) throws IOException {
		byte[] xmlBytes = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream(pathToXMLFile));
		
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
	                Header.header(HttpHeaderNames.CONTENT_LENGTH.toString(), xmlBytes.length),
	                Header.header(HttpHeaderNames.CACHE_CONTROL.toString(), "must-revalidate, post-check=0, pre-check=0")
	            )
	            .withBody(BinaryBody.binary(xmlBytes))
	    ); 
		
		ResponseEntity<T> response = restTemplate.exchange(request, clazz);
		
		// verify server has received exactly one request
		mockServerClient.verify(HttpRequest.request("/" + pathToXMLFile), VerificationTimes.once());
		
		return response;
	}	
}
