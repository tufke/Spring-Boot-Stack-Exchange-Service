package nl.merapar.stack.service.rest.problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import nl.merapar.stack.service.rest.exception.ServiceException;

/**
 * A common <code>ControllerAdvice</code> class to translate known Exceptions to standard Problem types.
 *
 */
@Slf4j
@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * The problem message if a validation failed.
     */
    private static final String MSG_VIOLATIONS = "Validation failed, see violations property for more details";

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Problem> handleServiceException(ServiceException ex, HttpServletRequest request) {
    	log.error(String.join("\n", ExceptionUtils.getRootCauseStackTrace(ex)));
    	return problem(HttpStatus.INTERNAL_SERVER_ERROR, request, Problem.TYPE_TRACEABLE_PROBLEM,
                ex.getMessage(), ex.getUuid(), ex.getCodeString(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM, ex.getMessage(), null, null, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Problem> handleValidationException(ValidationException ex, HttpServletRequest request) {
        return ex instanceof ConstraintViolationException ?
            problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_CONSTRAINT_VIOLATIONS,
                    MSG_VIOLATIONS, null, null, violations((ConstraintViolationException) ex))
                : problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM,
                    ex.getMessage(), null, null, null);
    }

    /**
     * This exception is thrown when class annotated with @Validated failed validation
     * The case is that validation failed for request parameters.
     * 
     * @param ex
     * @param request
     * @return ResponseEntity<Problem>
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Problem> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM,
            NestedExceptionUtils.getMostSpecificCause(ex).getMessage(), null, null, null);
    }      
    
    /**
     * This exception is thrown when try to set bean property with wrong type.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)    
    @Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
   		ResponseEntity<Object> problem = 
	    	problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM,
	                ex.getMessage(), null, null, null);
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
	}
    
    /**
     * This exception is thrown when try to set bean property with wrong type.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)    
    @Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
   		ResponseEntity<Object> problem = 
	    	problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM,
	                ex.getMessage(), null, null, null);
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
	}    
    
    
    /**
     * This exception is thrown when argument annotated with @Valid failed validation
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
   		ResponseEntity<Object> problem = 
   				problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_CONSTRAINT_VIOLATIONS,
   						MSG_VIOLATIONS, null, null, violations(ex.getBindingResult()));
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
 	}    
    
    /**
     * This exception is thrown when fatal binding errors occur
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
   		ResponseEntity<Object> problem = 
   				problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_CONSTRAINT_VIOLATIONS,
   						MSG_VIOLATIONS, null, null, violations(ex.getBindingResult()));
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
 	}        
    
    /**
     * This exception is thrown when request is missing a parameter
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getParameterName() + " parameter is missing";
   		ResponseEntity<Object> problem = 
   				problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM,
   						message, null, null, null);         
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
    }    
    
    /**
     * his exception is thrown when when the part of a multipart request is not found
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(
			MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getRequestPartName() + " request part of multipart request is missing";
   		ResponseEntity<Object> problem = 
   				problem(HttpStatus.BAD_REQUEST, request, Problem.TYPE_PROBLEM,
   						message, null, null, null);         
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
    }
    	
    /**
     * occurs when you send a requested with an unsupported HTTP method
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
     
   		ResponseEntity<Object> problem = 
   				problem(HttpStatus.METHOD_NOT_ALLOWED, request, Problem.TYPE_PROBLEM,
   						builder.toString(), null, null, null);         
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
    }    
    
    /**
     * occurs when the client send a request with unsupported media type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));
     
   		ResponseEntity<Object> problem = 
   				problem(HttpStatus.UNSUPPORTED_MEDIA_TYPE, request, Problem.TYPE_PROBLEM,
   						builder.toString(), null, null, null);         
   		return handleExceptionInternal(ex, problem, headers, problem.getStatusCode(), request);
    }    
    
    /**
     * Global exception handling, ie all other exceptions not forseen.
     * 
     * @param ex
     * @param request
     * @return ResponseEntity<Problem>
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @RequestMapping(produces = Problem.PROBLEM_JSON)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleRuntimeException(Exception ex, HttpServletRequest request) {
        log.error("Uncaught exception", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, request, Problem.TYPE_PROBLEM,
                NestedExceptionUtils.getMostSpecificCause(ex).getMessage(), null, null, null);
    }

    private ResponseEntity<Problem> problem(HttpStatus status, HttpServletRequest req, String type, String message,
                UUID id, String code, List<ConstraintViolation> cv) {

        // Accept header without json will result in status 406: prevent this by removing Accept values..
        req.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);

        return ResponseEntity.status(status)
            .contentType(Problem.MEDIA_TYPE_PROBLEM_JSON)
            .body(Problem.builder()
                .type(type)
                .status(status.value())
                .title(status.getReasonPhrase())
                .detail(message)
                .instance(req.getRequestURI())
                .id(id)
                .code(code)
                .violations(cv != null ? cv : Collections.emptyList())
                .build()
            );
    }

    private ResponseEntity<Object> problem(HttpStatus status, WebRequest req, String type, String message,
            UUID id, String code, List<ConstraintViolation> cv) {

	   	if(req instanceof ServletWebRequest && ((ServletWebRequest)req).getRequest() != null) {
		    HttpServletRequest request = ((ServletWebRequest)req).getRequest();
	   		
	   		// Accept header without json will result in status 406: prevent this by removing Accept values..
		    request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		
		    return ResponseEntity.status(status)
		        .contentType(Problem.MEDIA_TYPE_PROBLEM_JSON)
		        .body((Object)Problem.builder()
		            .type(type)
		            .status(status.value())
		            .title(status.getReasonPhrase())
		            .detail(message)
		            .instance(request.getRequestURI())
		            .id(id)
		            .code(code)
		            .violations(cv != null ? cv : Collections.emptyList())
		            .build()
		        );
	   	}
	   	
	   	return null;
}    
    
    private List<ConstraintViolation> violations(ConstraintViolationException cve) {
        return cve.getConstraintViolations().stream().map(cv -> ConstraintViolation.builder()
                .field(cv.getRootBeanClass().getName() + " " + cv.getPropertyPath())
                .message(cv.getMessage())
                .build()
        ).collect(Collectors.toList());
    }
    
    private List<ConstraintViolation> violations(BindingResult bindingresult) {
    	List<ConstraintViolation> result = new ArrayList<ConstraintViolation>();
    	
    	result.addAll(bindingresult.getFieldErrors().stream().map(h -> ConstraintViolation.builder()
                		.field(h.getField())
                		.message(h.getRejectedValue().toString())
                		.build()
    				  	).collect(Collectors.toList())
    				);
    	
    	result.addAll(bindingresult.getGlobalErrors().stream().map(h -> ConstraintViolation.builder()
                		.field(h.getObjectName())
                		.message(h.getDefaultMessage())
                		.build()
    					).collect(Collectors.toList())
    				);
    	return result;
    }

}