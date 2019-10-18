package nl.merapar.stack.service.rest.logging;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope("prototype")
public class RequestResponseLogger {

	@Autowired
	private ObjectMapper jacksonObjectMapper;  

	public void appendStartRequestMessage(StringBuilder msg) {
		if (msg != null) {
			msg.append("\n");
			msg.append(" * Server has received a request with body (POST, PUT etc) on thread ")
			   .append(Thread.currentThread().getName()).append("\n");
		}
	}
	
	public void appendStartRequestMessageSimple(StringBuilder msg) {
		if (msg != null) {
			msg.append("REQUEST");
		}
	}	
	
	public void appendStartResponseMessage(StringBuilder msg) {
		if (msg != null) {
    		msg.append("\n");
    		msg.append(" * Server responded with a response with body on thread ")
    		   .append(Thread.currentThread().getName()).append("\n");			
		}
	}
	
	public void appendStartResponseMessageSimple(StringBuilder msg) {
		if (msg != null) {
			msg.append("RESPONSE");
		}
	}		
	
	public void appendRequestURLAndQueryString(StringBuilder msg, HttpServletRequest request, boolean includeQueryString) {
		appendRequestPathAndQueryString(msg, request, includeQueryString, true, true);
	}
	
	public void appendRequestURIAndQueryString(StringBuilder msg, HttpServletRequest request, boolean includeQueryString) {
		appendRequestPathAndQueryString(msg, request, includeQueryString, false, false);
	}
	
	public void appendRequestPathAndQueryString(StringBuilder msg, HttpServletRequest request, boolean includeQueryString, boolean logFullURL, boolean addLineBreak) {
		if (msg != null) {
			msg.append(" > ").append(request.getMethod()).append(" ");
			if(logFullURL) {
				msg.append(request.getRequestURL().toString());
			}
			else {
				msg.append(request.getRequestURI().toString());
			}
			
			if (includeQueryString && StringUtils.isNotBlank(request.getQueryString())) {
				msg.append("?").append(request.getQueryString());
			}
			
			if(addLineBreak) {
				msg.append("\n");	
			}
		}
	}
	
	public void appendResponseHttpStatus(StringBuilder msg, HttpServletResponse response) {
		if (msg != null) {
    		msg.append(" <");
   			appendResponseHttpStatusSimple(msg, response);
    		msg.append("\n");
		}
	}
	
	public void appendResponseHttpStatusSimple(StringBuilder msg, HttpServletResponse response) {
		if (msg != null) {
    		HttpStatus status = HttpStatus.valueOf(response.getStatus());
    		if (status == null) {
    			msg.append(" ").append(response.getStatus());
    		} else {
    			msg.append(" ").append(status.value()).append(" (").append(status.getReasonPhrase()).append(")");
    		}		
		}
	}
	
	public void appendRequestHeaders(StringBuilder msg, HttpServletRequest request, boolean includeHeaders) {
		if (msg != null) {
			if (includeHeaders && request.getHeaderNames().hasMoreElements()) {
				Collections.list(request.getHeaderNames())
					.stream()
					.filter(h -> !h.equalsIgnoreCase("Content-Type"))
					.sorted()
					.forEach(h -> 
						msg.append(" > ").append(h).append(": ")
						.append( String.join(", ", Collections.list(request.getHeaders(h))) )
						.append("\n")
					);
			}
		}
	}
	
	public void appendResponseHeaders(StringBuilder msg, HttpServletResponse response, boolean includeHeaders) {
		if (msg != null) {
			if (includeHeaders && !response.getHeaderNames().isEmpty()) {
				response.getHeaderNames()
					.stream()
					.filter(h -> !h.equalsIgnoreCase("Content-Type"))
					.sorted()
					.forEach(h -> 
						msg.append(" > ").append(h).append(": ")
						.append( String.join(", ", response.getHeaders(h)) )
						.append("\n")
					);
			}
		}
	}	
	
	public void appendRequestPayloadAndContentType(StringBuilder msg, HttpServletRequest request, Object body, boolean includePayload) {
		if (msg != null) {
			msg.append(" > Content-Type: ").append(request.getContentType()).append("\n");
			if (includePayload) {
				String payload = getBodyAsString(body);
				if (StringUtils.isNotBlank(payload)) {
					msg.append(payload).append("\n");
				}
			}
		}
	}
	
	public void appendResponsePayloadAndContentType(StringBuilder msg, HttpServletResponse response, Object body, String cotenttype, boolean includePayload) {
		if (msg != null) {
			msg.append(" < Content-Type: ").append(cotenttype).append("\n");
			if (includePayload) {
				String payload = getBodyAsString(body);
				if (StringUtils.isNotBlank(payload)) {
					msg.append(payload).append("\n");
				}
			}
		}
	}
	
    private String getBodyAsString(Object body) {
    	if(body != null && jacksonObjectMapper.canSerialize(body.getClass())) {
	    	try {
				return jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
			} 
	    	catch (JsonProcessingException e) {
				//Swallow
			}
    	}
    	return "can not convert body to json, body = " + body;
    }
    
	public void logDebug(StringBuilder msg) {
		log.debug(msg.toString());   
	}
	
	public void logInfo(StringBuilder msg) {
		log.info(msg.toString());   
	}
}
