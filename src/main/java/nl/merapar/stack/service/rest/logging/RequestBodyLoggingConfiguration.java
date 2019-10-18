package nl.merapar.stack.service.rest.logging;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Configuration
@ConfigurationProperties("service.rest.logging")
@ConditionalOnProperty(name="service.rest.logging.enabled", havingValue = "true", matchIfMissing = true)
@ControllerAdvice
public class RequestBodyLoggingConfiguration extends RequestBodyAdviceAdapter {

    @Autowired
    private HttpServletRequest request;
    
	@Autowired
	private RequestResponseLogger logger;  
	
	
	/**
	 * Is the logging enabled? (default is false)
	 */
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
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}	
    
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
   	
    	if (shouldLog(request)) {
			StringBuilder msg = new StringBuilder();
			logger.appendStartRequestMessage(msg);
			logger.appendRequestURLAndQueryString(msg, request, includeQueryString);
			logger.appendRequestHeaders(msg, request, includeHeaders);
			logger.appendRequestPayloadAndContentType(msg, request, body, includePayload);
			logger.logInfo(msg);
		}
        
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
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
