package nl.merapar.stack.service.rest.resource.operation;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.merapar.stack.service.rest.exception.ServiceException;
import nl.merapar.stack.service.rest.mapper.AnalyzePostsResponseSOMapper;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsRequestSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;
import nl.merapar.stack.xml.parser.PostAnalysisParser;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope("prototype")
public class AnalyzePostsOperation extends AbstractOperation<AnalyzePostsRequestSO, AnalyzePostsResponseSO> {
	
	@Autowired
	private PostAnalysisParser parser;	
	
	@Autowired
	private AnalyzePostsResponseSOMapper mapper;
	
	@Override
	public AnalyzePostsResponseSO operate(final AnalyzePostsRequestSO input) {
		AnalyzePostsResponseSO output = null;
		try {
			PostsAnalysisAdapter report = parser.parse(input.getUrl());
			output = mapper.mapFrom(report);
		} 
		catch (XMLStreamException | IOException e) {
			throw new ServiceException(e);
		}
		
		return output;
	}

}
