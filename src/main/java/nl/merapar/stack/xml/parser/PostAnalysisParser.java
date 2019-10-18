package nl.merapar.stack.xml.parser;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.merapar.stack.xml.model.PostRow;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;


@Component
@Scope("prototype")
public class PostAnalysisParser extends AbstractParser<PostsAnalysisAdapter, PostRow> {

	
	public PostAnalysisParser(final PostsAnalysisAdapter parserAdapter) {
		super(parserAdapter, new PostRow());
	}

	@Autowired
	private ObjectMapper jacksonObjectMapper;
    
	public PostRow parseXML(final XMLStreamReader xmlReader) {
        
		Map<String, Object> map = new HashMap<String,Object>(); 
        if (xmlReader.isStartElement()) {
            for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
                final String localName = xmlReader.getAttributeLocalName(i);
                final String value = xmlReader.getAttributeValue(i);
                map.put(localName, value);
            }
        }
        
        PostRow model = jacksonObjectMapper.convertValue(map, PostRow.class);

        return model;
	}    
    

}
