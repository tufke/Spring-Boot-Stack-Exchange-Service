package nl.merapar.stack.service.rest.resource;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsRequestSO;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;
import nl.merapar.stack.service.rest.resource.operation.AnalyzePostsOperation;

@RestController
@RequestMapping("stack/posts")
@RequestScope
@Validated
@OpenAPIDefinition
public class StackPostsResource {

	@Autowired
	private AnalyzePostsOperation analyzePostsoperation;
	
	
	@RequestMapping(value="/analyze", method=RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
    public AnalyzePostsResponseSO analyzePosts(@Valid @RequestBody AnalyzePostsRequestSO request) {
		return analyzePostsoperation.operate(request);
    }


}


