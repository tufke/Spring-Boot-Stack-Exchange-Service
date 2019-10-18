package nl.merapar.stack.xml.parser.test;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.netty.handler.codec.http.HttpHeaderNames;
import nl.merapar.stack.xml.parser.AbstractParser;
import nl.merapar.stack.xml.parser.adapter.AbstractParserAdapter;

/**
 * 
 * Start an embedded server so we can use the RestTemplate to call the mockserver
 *
 */
public abstract class AbstractParserTest {

	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this, 9000);
 
	protected MockServerClient mockServerClient;	
	
	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	public abstract AbstractParser<?,?> getParser();
	
	
	protected AbstractParserAdapter<?> parseHttp(String pathToXMLFile) throws XMLStreamException, IOException {
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
		
		AbstractParserAdapter<?> adapter = getParser().parse("http://localhost:9000/" + pathToXMLFile);
		
		// verify server has received exactly one request
		mockServerClient.verify(HttpRequest.request("/" + pathToXMLFile), VerificationTimes.once());
		
		return adapter;
	}
	
	protected AbstractParserAdapter<?> parseFile(String pathToXMLFile) throws XMLStreamException, IOException {
		URL url = getClass().getClassLoader().getResource(pathToXMLFile);
		AbstractParserAdapter<?> adapter = getParser().parse(url.toString());
		
		return adapter;
	}
	
}
