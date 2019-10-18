package nl.merapar.stack.service.rest.itest;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import nl.merapar.stack.service.rest.resource.api.AbstractServiceObject;

public class AbstractIntegrationTest {

	@Autowired
    protected TestRestTemplate restTemplate;

	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	protected <T extends AbstractServiceObject, S extends AbstractServiceObject> ResponseEntity<T> doRequest(RequestEntity<S> request, Class<T> clazz) throws IOException {
		ResponseEntity<T> response = restTemplate.exchange(request, clazz);
		
		return response;
	}	
}
