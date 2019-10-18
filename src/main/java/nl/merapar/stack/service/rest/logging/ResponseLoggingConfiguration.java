package nl.merapar.stack.service.rest.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Configuration
@ConfigurationProperties("service.rest.logging")
@ConditionalOnProperty(name="service.rest.logging.enabled", havingValue = "true", matchIfMissing = true)
@ControllerAdvice
public class ResponseLoggingConfiguration implements ResponseBodyAdvice<Object> {

	@Autowired
	private RequestResponseLogger logger;   
	
	/**
	 * Is the logging enabled? (default is false)
	 */
	@SuppressWarnings("unused")
	private boolean enabled = true;
    
	/**
	 * Specify regex patterns for path Uri's to exclude from logging, e.g. '/app/health' (default is no exclusions)
	 */
	private Map<String, String> excludeUriPattern = new HashMap<>();

	/**
	 * Specify regex patterns for path Uri's to include from logging, e.g. '.*\/api\/.*' 
	 */
	private Map<String, String> includeUriPattern = new HashMap<>();

	/**
	 * Include HTTP Header information in request/response logging? (default is true)
	 */
	private boolean includeHeaders = true;
	
	/**
	 * Include HTTP Query String information in request logging? (default is true)
	 */
	private boolean includeQueryString = true;	
	
	/**
	 * Include HTTP Payload in request/response logging? (default is true)
	 */
	private boolean includePayload = true;
	

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}
    
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest requestServer,
			ServerHttpResponse responseServer) {

        if (requestServer instanceof ServletServerHttpRequest && responseServer instanceof ServletServerHttpResponse) {
        	HttpServletRequest request = ((ServletServerHttpRequest) requestServer).getServletRequest();
        	HttpServletResponse response = ((ServletServerHttpResponse) responseServer).getServletResponse();
        	
    		if (shouldLog(request)) {
    			StringBuilder msg = new StringBuilder();
    			logger.appendStartResponseMessage(msg);
    			logger.appendRequestURLAndQueryString(msg, request, includeQueryString);
    			logger.appendResponseHttpStatus(msg, response);
    			logger.appendResponseHeaders(msg, response, includeHeaders);
    			logger.appendResponsePayloadAndContentType(msg, response, body, selectedContentType.toString(), includePayload);
    			logger.logInfo(msg);
    		}        	
        }
        
        return body;

	}    	

 	private boolean shouldLog(ServletRequest request) {
		String requestURI = ((HttpServletRequest) request).getRequestURI();
		return isIncluded(requestURI) && !isExcluded(requestURI);
	}

	private boolean isExcluded(String requestURI) {
		return this.excludeUriPattern.values().stream().anyMatch(p -> Pattern.matches(p, requestURI));
	}

	private boolean isIncluded(String requestURI) {
		return this.includeUriPattern.isEmpty() || this.includeUriPattern.values().stream().anyMatch(p -> Pattern.matches(p, requestURI));
	}



}
