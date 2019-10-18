package nl.merapar.stack.xml.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import nl.merapar.stack.service.rest.exception.ServiceException;
import nl.merapar.stack.util.URLConnectionUtil;
import nl.merapar.stack.xml.model.AbstractModel;
import nl.merapar.stack.xml.parser.adapter.AbstractParserAdapter;

@Slf4j
@Component
public abstract class AbstractParser<T extends AbstractParserAdapter<S>, S extends AbstractModel> {

	private T parserAdapter;
	private S model;

	@Autowired
	URLConnectionUtil conncetionUtil;
	
	public AbstractParser(final T parserAdapter, final S model) {
		super();
		this.parserAdapter = parserAdapter;
		this.model = model;
	}

	protected abstract S parseXML(final XMLStreamReader xmlReader);
	
    public T parse(final String url) throws XMLStreamException, IOException {

    	log.debug(" ==> Start " + this.getClass().getName() + ".parse");
    	log.debug(" ==> parse INPUT: " + url);
    	
        // create xml event reader for input stream, Set buffer to 512Kb
      	URLConnection con = conncetionUtil.getUrlConnection(url);
    	InputStream inputStream = new BufferedInputStream(con.getInputStream(), 524288);
    	XMLStreamReader xmlReader = null;
    	
    	try {
	    	xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(new StreamSource(inputStream));
	     	
	        long count = 0;
	        while(xmlReader.hasNext()) {
	
	            // get the next event and process it.
	            xmlReader.next();
	            if (xmlReader.isStartElement() && xmlReader.getLocalName().equals(model.getQName().getLocalPart())) {
	            	count++;
	                
	            	parserAdapter.process(parseXML(xmlReader));
	            	
	                if (count % 100000 == 0) {
	                	log.debug("records geanalyseerd : " + count);
	                }                   	
	            }
	        }    	
    	}
	    catch(XMLStreamException e) {
	    	throw new ServiceException("File is not an XML file or the XML is not wellformed", e);
	    }
	    finally {
	        if(inputStream != null) {
	        	inputStream.close();
	        }
	        if(xmlReader != null) {
	        	xmlReader.close();
	        }
		}
        
        log.debug(" <== parse OUTPUT: " + parserAdapter);
        log.debug(" <== " + this.getClass().getName() + ".parse klaar");
        
        return parserAdapter;
    }
    

}
