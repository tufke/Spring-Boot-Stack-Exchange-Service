package nl.merapar.stack.service.rest.logging;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import lombok.Data;

/**
 * Auto configure logging.
 * 
 */
@Data
@Configuration
@ConfigurationProperties("service.rest.logging.executiontime")
@ConditionalOnProperty(name="service.rest.logging.executiontime.enabled", havingValue = "true", matchIfMissing = false)
@Component
class LoggingFilterConfiguration {
	
	@Autowired
	private RequestResponseLogger log;  
	
	/**
	 * Is the MDC logging enabled? (default is false)
	 */
	private boolean enabled = false;

	/**
	 * Specify regex patterns for path Uri's to exclude from logging, e.g. '/stack/posts/analyze' (default is no exclusions)
	 */
	private Map<String, String> excludeUriPattern = new HashMap<>();

	/**
	 * Specify regex patterns for path Uri's to include from logging, e.g. '.*\/stack\/.*' 
	 */
	private Map<String, String> includeUriPattern = new HashMap<>();

    @Bean(name = "LoggingFilter")
    public Filter filter() {
    	return new GenericFilterBean() {

    		@Override
    		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    			final Instant start = Instant.now();
    			
				// Log start
				if (shouldLog(request)) {
					HttpServletRequest req = (HttpServletRequest) request;
    				
    				StringBuilder msg = new StringBuilder();
    				log.appendStartRequestMessageSimple(msg);
    				log.appendRequestURIAndQueryString(msg, req, true);
    				log.logDebug(msg);
				}
    		
				// Forward request
				chain.doFilter(request, response);

				// Log result
				if (shouldLog(request) && response instanceof HttpServletResponse) {
					HttpServletRequest req = (HttpServletRequest) request;
					HttpServletResponse res = (HttpServletResponse) response;
					
					final Instant end = Instant.now();
					final Duration duration = Duration.between(start, end);

					String timetaken = null;
		            if (duration.getSeconds() > 0) {
		            	timetaken = duration.getSeconds() + " sec";
		            }
		            else {
		            	timetaken = duration.toMillis() + " ms";
		            }    					
					
	    			StringBuilder msg = new StringBuilder();
	    			log.appendStartResponseMessageSimple(msg);
	    			msg.append(" [" + timetaken + "]");
	    			log.appendResponseHttpStatusSimple(msg, res);
	    			log.appendRequestURIAndQueryString(msg, req, true);
    				msg.append(" content-type=" + res.getContentType());
    				log.logDebug(msg);
				}
    			
    		}

    	};
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