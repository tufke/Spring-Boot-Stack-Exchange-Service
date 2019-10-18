package nl.merapar.stack.service.rest.resource.operation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import nl.merapar.stack.service.rest.exception.ServiceException;
import nl.merapar.stack.service.rest.mapper.AnalyzePostsResponseSOMapper;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsDetailsSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsRequestSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;
import nl.merapar.stack.service.rest.resource.operation.AnalyzePostsOperation;
import nl.merapar.stack.xml.parser.PostAnalysisParser;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;

@RunWith(MockitoJUnitRunner.class) //JUnit Runner which causes all the initialization magic with @Mock and @InjectMocks to happen
public class AnalyzePostsOperationTest {

    @Mock
	private PostAnalysisParser parser;	
	
	@Mock
	private AnalyzePostsResponseSOMapper mapper;    

    @InjectMocks
    private AnalyzePostsOperation operation;

    
    @Test
    public void testSuccess() throws XMLStreamException, IOException {
    	AnalyzePostsRequestSO operationInput = getOperationInput();
    	String parserInput = getParserInput(operationInput);
    	PostsAnalysisAdapter parserOutput = getParserOutput();
    	PostsAnalysisAdapter mapperInput = parserOutput;
    	AnalyzePostsResponseSO mapperOutput = getMapperOutput(parserOutput);
 	
		when(parser.parse(ArgumentMatchers.eq(parserInput))).thenReturn(parserOutput);
		when(mapper.mapFrom(ArgumentMatchers.eq(mapperInput))).thenReturn(mapperOutput);
		
		AnalyzePostsResponseSO output = operation.operate(operationInput);
		
		verify(parser).parse(parserInput);
		verify(mapper).mapFrom(mapperInput);
		
		assertNotNull("output empty", output);
		assertEquals("Incorrect output", mapperOutput, output);
    }
    
    @Test(expected = ServiceException.class)
    public void testServiceException_IOException() throws XMLStreamException, IOException {
    	AnalyzePostsRequestSO operationInput = getOperationInput();
    	
    	when(parser.parse(ArgumentMatchers.any())).thenThrow(new IOException("message"));
    	
    	try {
    		operation.operate(operationInput);
    	}
    	catch(ServiceException e) {
    		assertEquals("message", e.getCause().getMessage());
    		throw e;
    	}
    	
    	fail("ServiceException expected");
    }    
    
    @Test(expected = ServiceException.class)
    public void testServiceException_XMLStreamException() throws XMLStreamException, IOException {
    	AnalyzePostsRequestSO operationInput = getOperationInput();
    	
    	when(parser.parse(ArgumentMatchers.any())).thenThrow(new XMLStreamException("message"));
    	
    	try {
    		operation.operate(operationInput);
    	}
    	catch(ServiceException e) {
    		assertEquals("message", e.getCause().getMessage());
    		throw e;
    	}
    	
    	fail("ServiceException expected");
    }    
    
    
    private AnalyzePostsRequestSO getOperationInput() {
    	return AnalyzePostsRequestSO.builder()
    	    	.url("")
    	    	.build();
    }
    
    private String getParserInput(AnalyzePostsRequestSO operationInput) {
    	return operationInput.getUrl();
    }
    
    private PostsAnalysisAdapter getParserOutput() {
    	return PostsAnalysisAdapter.builder()
        		.firstPost(LocalDateTime.now())
        		.lastPost(LocalDateTime.now())
        		.totalAcceptedPosts(BigInteger.TEN)
        		.totalPosts(BigInteger.TEN)
        		.totalScore(new BigInteger("100"))
        		.avgScore(10)
        		.build();
    }
    
    private AnalyzePostsResponseSO getMapperOutput(PostsAnalysisAdapter parserOutput) {
    	AnalyzePostsDetailsSO details =
    		AnalyzePostsDetailsSO.builder()
				.firstPost(parserOutput.getFirstPost())
				.lastPost(parserOutput.getLastPost())
				.avgScore(parserOutput.getAvgScore())
				.totalAcceptedPosts(parserOutput.getTotalAcceptedPosts())
				.totalPosts(parserOutput.getTotalPosts())
				.build();   
    	
    	return AnalyzePostsResponseSO.builder()
        		 .details(details)
        		 .build();
    }
}
